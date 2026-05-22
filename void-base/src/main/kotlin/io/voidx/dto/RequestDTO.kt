package io.voidx.dto

import io.voidx.Method
import io.voidx.error
import io.voidx.router.Router
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.net.URI
import java.net.http.HttpRequest
import java.util.*

/**
 * Immutable representation of an HTTP request handled by the server.
 *
 * Fields include the HTTP [method], request [target] (path + optional query),
 * request [headers], and raw [body].
 *
 * Attributes map:
 * - [attributes] provides a per-request scratchpad for middleware and handlers to attach
 *   arbitrary data (e.g., a trace ID). Keys are convention-based; prefer small, unique names
 *   or extension properties (see `RequestDTO.traceId`).
 */
data class RequestDTO(
    /** HTTP method of the request (GET, POST, ...). */
    val method: Method,
    /** Path and optional query (e.g., "/users?id=1"). */
    val target: String,
    /** Immutable view of request headers (first-value per name). */
    val headers: Map<String, String>,
    /** Raw request body as a string. */
    val body: String,
) {
    /**
     * Per-request, mutable bag for attaching values during processing.
     * Intended for internal use by middleware and handlers.
     */
    val attributes: MutableMap<String, Any> = mutableMapOf()

    /**
     * Parsed cookies from the "Cookie" header.
     */
    val cookies: Map<String, String>
        get() {
            val percentRegex = "%([0-9A-Fa-f]{2})".toRegex()
            val pairs = headers["Cookie"]?.split(";") ?: return mapOf()
            val map = mutableMapOf<String, String>()
            for (it in pairs) {
                val pair = it.trim().split("=", limit = 2)
                if (pair.size != 2) continue
                val name = pair[0].trim()
                val value =
                    percentRegex.replace(pair[1].trim()) { match ->
                        match.groupValues[1]
                            .toInt(16)
                            .toChar()
                            .toString()
                    }
                map[name] = value
            }
            return map
        }

    companion object {
        /**
         * Parses an incoming HTTP request from the given [socket] and [router] into a [RequestDTO].
         * In case of malformed requests, the [Socket.error] path is invoked
         * and a minimal default GET request is returned to allow graceful handling.
         */
        internal fun parse(
            socket: Socket,
            router: Router
        ): RequestDTO {
            val inputStream = socket.getInputStream()
            val headers: MutableMap<String, String> = mutableMapOf()
            val method: Method
            val path: String
            val reader = BufferedReader(InputStreamReader(inputStream))
            val line = reader.readLine()?.split(" ") ?: throw IllegalStateException("Empty request received")
            try {
                if (line.size < 2) throw IllegalArgumentException("Invalid request line")
                method = Method.valueOf(line[0].uppercase(Locale.getDefault()))
            } catch (e: Exception) {
                socket.error(1.1, router, e)
                return buildRequest {
                    this.method = Method.GET
                    target = "/"
                    this.headers.putAll(headers)
                    body = ""
                } // Provide a default request
            }
            path = line[1]

            var headerLine: String?
            while ((reader.readLine().also { headerLine = it }) != null && headerLine!!.isNotEmpty()) {
                val header = headerLine.split(": ", limit = 2)
                if (header.size == 2) headers[header[0]] = header[1]
            }

            val body = StringBuilder()
            val contentLength = headers["Content-Length"]?.toIntOrNull()
            // Per RFC 7231, a payload on GET has no defined semantics; many servers ignore it.
            // We choose to ignore the body for GET requests even if Content-Length is present.
            if (method.name != "GET" && contentLength != null && contentLength > 0) {
                val charArray = CharArray(contentLength)
                reader.read(charArray, 0, contentLength)
                body.append(charArray)
            }

            val requestDTO: RequestDTO =
                buildRequest {
                    this.method = method
                    this.target = path
                    this.headers.putAll(headers)
                    this.body = body.toString()
                }

            return requestDTO
        }

        /**
         * Parses an incoming HTTP request from the given [inputStream] into a [RequestDTO].
         * This overload will return a minimal default GET request on parse errors.
         */
        fun parse(inputStream: InputStream): RequestDTO {
            val headers: MutableMap<String, String> = mutableMapOf()
            val method: Method
            val path: String
            val reader = BufferedReader(InputStreamReader(inputStream))
            val line =
                reader.readLine()?.split(" ") ?: return buildRequest {
                    this.method = Method.GET
                    target = "/"
                    this.headers.putAll(headers)
                    body = ""
                }
            try {
                if (line.size < 2) throw IllegalArgumentException("Invalid request line")
                method = Method.valueOf(line[0].uppercase(Locale.getDefault()))
            } catch (_: Exception) {
                return buildRequest {
                    this.method = Method.GET
                    target = "/"
                    this.headers.putAll(headers)
                    body = ""
                }
            }
            path = line[1]

            var headerLine: String?
            while ((reader.readLine().also { headerLine = it }) != null && headerLine!!.isNotEmpty()) {
                val header = headerLine.split(": ", limit = 2)
                if (header.size == 2) headers[header[0]] = header[1]
            }

            val body = StringBuilder()
            val contentLength = headers["Content-Length"]?.toIntOrNull()
            if (method.name != "GET" && contentLength != null && contentLength > 0) {
                val charArray = CharArray(contentLength)
                reader.read(charArray, 0, contentLength)
                body.append(charArray)
            }

            return buildRequest {
                this.method = method
                this.target = path
                this.headers.putAll(headers)
                this.body = body.toString()
            }
        }
    }

    /** Returns the value of the header named [headerName], or null if it is not present. */
    operator fun get(headerName: String): String? {
        // Fast path: exact match
        headers[headerName]?.let { return it }
        // Fallback: case-insensitive lookup as HTTP header field-names are case-insensitive (RFC 7230 §3.2)
        val key = headers.keys.firstOrNull { it.equals(headerName, ignoreCase = true) }
        return key?.let { headers[it] }
    }
}

/**
 * Mutable builder for constructing a [RequestDTO] in tests or internal fallbacks.
 */
class RequestBuilder {
    var method: Method = Method.GET
    var target: String = "/"
    val headers: MutableMap<String, String> = mutableMapOf()
    var body: String = ""

    /** Builds an immutable [RequestDTO] from the current builder state. */
    fun build(): RequestDTO = RequestDTO(method, target, headers.toMap(), body)
}

/**
 * DSL helper to build a [RequestDTO] using a [RequestBuilder].
 */
fun buildRequest(builder: RequestBuilder.() -> Unit): RequestDTO {
    val build = RequestBuilder()
    build.builder()
    return build.build()
}

/**
 * DSL helper to apply mutations to the headers map in the [RequestBuilder].
 */
fun RequestBuilder.headers(block: MutableMap<String, String>.() -> Unit) {
    headers.block()
}

/**
 * Converts this high-level [RequestDTO] into a Java [HttpRequest] using the provided base [url].
 *
 * Behavior:
 * - The request URI is built by simple concatenation of `url + target` (e.g., `"http://host" + "/path"`).
 *   Ensure that `url` includes the scheme/host/port and that `target` begins with `/`.
 * - The HTTP method is taken from [RequestDTO.method].
 * - For GET requests, always uses `noBody()` (ignores any provided body for RFC alignment).
 * - For other methods, if [RequestDTO.body] is non-empty, it uses `BodyPublishers.ofString(body)`; otherwise `noBody()`.
 * - All entries from [RequestDTO.headers] are forwarded to the request via `builder.header(key, value)`.
 *
 * Notes:
 * - `URI.create(url + target)` may throw [IllegalArgumentException] if the resulting string is not a valid URI.
 * - No additional URL encoding is performed here. Pre-encode path/query segments in [RequestDTO.target] if needed.
 *
 * Example:
 * ```kotlin
 * val req = buildRequest {
 *     method = Method.POST
 *     target = "/api/items"
 *     headers { put("Content-Type", "application/json") }
 *     body = "{""name"":""Widget""}"
 * }
 * val httpRequest = req.toHttpRequest("http://localhost:8080")
 * ```
 */
fun RequestDTO.toHttpRequest(url: String): HttpRequest {
    val builder =
        HttpRequest
            .newBuilder()
            .uri(URI.create(url + target))

    // For GET, prefer the dedicated GET() builder to ensure bodyPublisher() is empty
    if (method == Method.GET) {
        builder.GET()
    } else {
        val publisher =
            if (body.isNotEmpty()) HttpRequest.BodyPublishers.ofString(body) else HttpRequest.BodyPublishers.noBody()
        builder.method(method.name, publisher)
    }

    headers.forEach { (key, value) -> builder.header(key, value) }

    return builder.build()
}
