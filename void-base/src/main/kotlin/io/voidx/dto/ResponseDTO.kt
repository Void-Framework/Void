package io.voidx.dto

import java.io.File
import java.io.OutputStream
import java.io.PrintWriter
import java.net.URLConnection
import java.nio.file.Files

/** Mutable map of HTTP header names to values. */
typealias Headers = MutableMap<String, String>

/**
 * Mutable representation of an HTTP response to be written back to the client.
 *
 * - [status] and [statusText] form the status line.
 * - [headers] holds response headers (auto-populated with Content-Length if missing).
 * - [body] is a [ResponseBody] wrapping either a String or ByteArray payload.
 */
data class ResponseDTO(
    /** Numeric HTTP status code (e.g., 200, 404). */
    var status: Int,
    /** Short reason phrase for the status line (e.g., "OK"). */
    var statusText: String,
    /** Response body wrapper, either [ResponseBody.StringBody] or [ResponseBody.ByteArrayBody]. */
    var body: ResponseBody<*>,
) {
    /**
     * Per-response, mutable bag for attaching values during processing.
     * Intended for internal use by middleware and handlers.
     */
    val attributes: MutableMap<String, Any> = mutableMapOf()

    /** Mutable map of response headers. "Content-Length" will be added if missing during write. */
    var headers = mutableMapOf<String, String>()

    /**
     * Set of cookies to be included in the response via "Set-Cookie" headers.
     */
    val cookies = mutableListOf<Cookie>()

    /**
     * Back-reference to the originating [RequestDTO].
     * Set internally by the router so middleware can correlate requests and responses.
     */
    internal lateinit var _request: RequestDTO

    /** Public accessor for the originating request; valid after routing sets it. */
    val request: RequestDTO
        get() = _request

    internal operator fun set(
        headerName: String,
        headerValue: String,
    ) = headers.put(headerName, headerValue)
}

/**
 * Generic builder interface used by [buildResponse] to produce a [ResponseDTO]
 * for either String or ByteArray bodies.
 */
interface ResponseBuilder<T> {
    var status: Int
    var statusText: String
    var headers: MutableMap<String, String>
    var cookies: MutableList<Cookie>
    var body: T
}

/** Builder for string-based HTTP responses. */
class StringResponseBuilder : ResponseBuilder<String> {
    override var status: Int = 200
    override var statusText: String = "OK"
    override var headers: MutableMap<String, String> = mutableMapOf()
    override var body: String = ""
    override var cookies: MutableList<Cookie> = mutableListOf()

    fun build(): ResponseDTO =
        ResponseDTO(status, statusText, ResponseBody.StringBody(body)).apply {
            headers =
                this@StringResponseBuilder.headers
            cookies.addAll(this@StringResponseBuilder.cookies)
        }
}

/** Builder for binary (ByteArray) HTTP responses. */
class ByteResponseBuilder : ResponseBuilder<ByteArray> {
    override var status: Int = 200
    override var statusText: String = "OK"
    override var headers: MutableMap<String, String> = mutableMapOf()
    override var body: ByteArray = ByteArray(1)
    override var cookies: MutableList<Cookie> = mutableListOf()

    fun build(): ResponseDTO =
        ResponseDTO(status, statusText, ResponseBody.ByteArrayBody(body)).apply {
            headers =
                this@ByteResponseBuilder.headers
            cookies.addAll(this@ByteResponseBuilder.cookies)
        }
}

/**
 * Builds a [ResponseDTO] using a type-safe builder for either String or ByteArray bodies.
 * The generic [T] determines which underlying builder is used.
 */
inline fun <reified T> buildResponse(builder: ResponseBuilder<T>.() -> Unit): ResponseDTO {
    @Suppress("UNCHECKED_CAST")
    val build =
        when (T::class) {
            String::class -> StringResponseBuilder()
            ByteArray::class -> ByteResponseBuilder()
            else -> throw IllegalArgumentException("Unsupported response type: ${T::class}")
        } as ResponseBuilder<T>
    build.builder()
    return when (build) {
        is StringResponseBuilder -> build.build()
        is ByteResponseBuilder -> build.build()
        else -> throw IllegalStateException("Unknown builder type")
    }
}

/**
 * Applies the given header mutation block to this builder's headers.
 *
 * @param block A receiver lambda that mutates the builder's mutable headers map.
 */
fun ResponseBuilder<*>.headers(block: Headers.() -> Unit) {
    headers.block()
}

/**
 * Creates a new mutable headers map and applies the provided DSL block to it.
 *
 * @param block A lambda with receiver applied to the newly created `Headers` map to populate or modify entries.
 * @return The populated `Headers` map.
 */
fun headers(block: Headers.() -> Unit): Headers {
    val map = mutableMapOf<String, String>()
    map.block()
    return map
}

/**
 * Writes the ResponseDTO to this OutputStream as an HTTP/1.x message using the specified version.
 *
 * The function writes the status line, headers, `Set-Cookie` lines for any cookies, a blank line, and the response body.
 * If `Content-Length` is not present it will be set based on the response body size.
 *
 * Supports `ResponseBody.StringBody` and `ResponseBody.ByteArrayBody` payloads.
 *
 * @param response The response to serialize and send.
 * @param version The HTTP major.minor version number to use in the status line (for example `1.1`).
 */
fun OutputStream.writeHTTP(
    response: ResponseDTO,
    version: Number,
) {
    val writer = PrintWriter(this, true)
    writer.println("HTTP/$version ${response.status} ${response.statusText}")

    val responseBody = response.body
    if (!response.headers.containsKey("Content-Length")) {
        when (responseBody) {
            is ResponseBody.StringBody -> {
                response["Content-Length"] =
                    responseBody.body
                        .toByteArray()
                        .size
                        .toString()
            }

            else -> {
                response["Content-Length"] = (responseBody.body as ByteArray).size.toString()
            }
        }
    }

    for ((key, value) in response.headers.entries) {
        writer.println("$key: $value")
    }
    for (cookie in response.cookies) {
        writer.println("Set-Cookie: $cookie")
    }
    writer.println()
    writer.flush()

    when (val body = response.body) {
        is ResponseBody.StringBody -> {
            writer.print(body.body)
            writer.flush()
        }

        is ResponseBody.ByteArrayBody -> {
            this.write(body.body)
            this.flush()
        }
    }
}

/**
 * Create an HTTP 200 OK response with an empty string body.
 *
 * @return A ResponseDTO with status 200, statusText "OK", empty string body, and default headers and cookies.
 */
fun emptyResponse(): ResponseDTO = buildResponse<String> { }

/**
 * Represents the body of an HTTP response, specializing in [String] or [ByteArray].
 */
sealed class ResponseBody<T>(
    open val body: T,
) {
    class StringBody internal constructor(
        override val body: String,
    ) : ResponseBody<String>(body)

    class ByteArrayBody internal constructor(
        override val body: ByteArray,
    ) : ResponseBody<ByteArray>(body)
}

/**
 * Create an HTTP 200 OK response using the provided body, headers, and cookies.
 *
 * The response will have status `200` and statusText `"OK"`. Supported body types are `String` and
 * `ByteArray`; other types will cause an `IllegalArgumentException`.
 *
 * @param body The response payload.
 * @param headers HTTP headers to set on the response.
 * @param cookies Cookies to include as `Set-Cookie` entries.
 * @return A `ResponseDTO` with status 200, statusText `"OK"`, and the supplied body, headers, and cookies.
 */
inline fun <reified T> ok(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 200
        this.statusText = "OK"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 201 Created response containing the provided body.
 *
 * @param body The response payload.
 * @param headers Headers to include in the response.
 * @param cookies Cookies to emit as `Set-Cookie` headers.
 * @return A ResponseDTO with status 201, statusText "Created", the given body, and the provided headers and cookies.
 */
inline fun <reified T> created(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 201
        this.statusText = "Created"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 202 Accepted response containing the provided body.
 *
 * @param body The response payload.
 * @param headers HTTP headers to include in the response.
 * @param cookies Cookies to include as `Set-Cookie` headers.
 * @return A ResponseDTO with status 202, statusText "Accepted", and the supplied body, headers, and cookies.
 */
inline fun <reified T> accepted(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 202
        this.statusText = "Accepted"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 204 No Content response.
 *
 * @param headers Header map to assign to the response.
 * @param cookies Cookies to include in the response.
 * @return A ResponseDTO with status 204, statusText "No Content", and an empty body.
 */
fun noContent(
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse<String> {
        this.status = 204
        this.statusText = "No Content"
        this.body = ""
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP redirect response pointing to the given location.
 *
 * @param location The target URL placed in the `Location` response header.
 * @param permanent When `true`, the response status is 301 ("Moved Permanently"); otherwise 302 ("Found").
 * @param cookies Cookies to include in the response's `Set-Cookie` headers.
 * @return A `ResponseDTO` with the appropriate redirect status, a `Location` header set to `location`, an empty body, and any provided cookies.
 */
fun redirect(
    location: String,
    permanent: Boolean = false,
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse<String> {
        this.status = if (permanent) 301 else 302
        this.statusText = if (permanent) "Moved Permanently" else "Found"
        this.headers = mutableMapOf("Location" to location)
        this.body = ""
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 307 Temporary Redirect response targeting the specified location.
 *
 * @param location The URL to set in the `Location` response header.
 * @param cookies Cookies to include in the response's `Set-Cookie` headers.
 * @return A ResponseDTO with status 307 ("Temporary Redirect"), the `Location` header set to `location`, an empty body, and any provided cookies.
 */
fun temporaryRedirect(
    location: String,
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse<String> {
        this.status = 307
        this.statusText = "Temporary Redirect"
        this.headers = mutableMapOf("Location" to location)
        this.body = ""
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 308 Permanent Redirect response that points to the specified location.
 *
 * @param location The value to set for the `Location` response header.
 * @param cookies Additional cookies to include in the response.
 * @return A ResponseDTO with status 308, statusText "Permanent Redirect", the `Location` header set to `location`, an empty body, and the provided cookies.
 */
fun permanentRedirect(
    location: String,
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse<String> {
        this.status = 308
        this.statusText = "Permanent Redirect"
        this.headers = mutableMapOf("Location" to location)
        this.body = ""
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 400 Bad Request response using the provided body.
 *
 * The resulting response has status 400 and statusText "Bad Request", and includes
 * the supplied headers and cookies.
 *
 * @param body The response payload.
 * @param headers HTTP headers to include in the response.
 * @param cookies Cookies to include as `Set-Cookie` entries.
 * @return A ResponseDTO with status 400, statusText "Bad Request", and the supplied body, headers, and cookies.
 */
inline fun <reified T> badRequest(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 400
        this.statusText = "Bad Request"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 401 Unauthorized response containing the provided body.
 *
 * @param body The response payload.
 * @param headers HTTP headers to include in the response.
 * @param cookies Cookies to include as `Set-Cookie` headers.
 * @return A ResponseDTO with status 401 ("Unauthorized") containing the given body, headers, and cookies.
 */
inline fun <reified T> unauthorized(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 401
        this.statusText = "Unauthorized"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create a 403 Forbidden HTTP response using the provided body, headers, and cookies.
 *
 * @param body The response payload. Only `String` and `ByteArray` are supported as response body types.
 * @param headers HTTP headers to set on the response; the builder's headers will be replaced with this map.
 * @param cookies Cookies to include as `Set-Cookie` entries on the response.
 * @return A `ResponseDTO` with status `403` and statusText `"Forbidden"` containing the supplied body, headers, and cookies.
 */
inline fun <reified T> forbidden(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 403
        this.statusText = "Forbidden"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Creates a 404 Not Found HTTP response containing the provided body, headers, and cookies.
 *
 * @return A ResponseDTO with status 404 and statusText "Not Found" whose body, headers, and cookies are set to the given values.
 */
inline fun <reified T> notFound(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 404
        this.statusText = "Not Found"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 409 Conflict response with the provided payload.
 *
 * @param body The response payload to set on the returned DTO.
 * @param headers Response headers to include in the returned DTO.
 * @param cookies Cookies to include in the returned DTO.
 * @return A ResponseDTO with status 409, statusText "Conflict", and the provided body, headers, and cookies.
 */
inline fun <reified T> conflict(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 409
        this.statusText = "Conflict"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 429 Too Many Requests response.
 *
 * @param body The response payload.
 * @param headers Headers to include in the response.
 * @param cookies Cookies to include as `Set-Cookie` headers.
 * @return A ResponseDTO with status 429, statusText "Too Many Requests", and the provided body, headers, and cookies.
 */
inline fun <reified T> tooManyRequests(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 429
        this.statusText = "Too Many Requests"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create a 500 Internal Server Error response using the provided body.
 *
 * @param body The response payload.
 * @param headers HTTP header name/value pairs to include in the response.
 * @param cookies Cookies to emit as `Set-Cookie` headers.
 * @return A ResponseDTO with status 500 and status text "Internal Server Error" containing the provided body, headers, and cookies.
 */
inline fun <reified T> internalServerError(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 500
        this.statusText = "Internal Server Error"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create a 501 Not Implemented HTTP response containing the provided body.
 *
 * @param body The response payload.
 * @param headers Headers to include in the response; assigned directly to the response's header map.
 * @param cookies Cookies to include; appended to the response's cookie list.
 * @return A ResponseDTO with status `501`, statusText `"Not Implemented"`, and the provided body, headers, and cookies.
 */
inline fun <reified T> notImplemented(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 501
        this.statusText = "Not Implemented"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 502 Bad Gateway response using the provided body.
 *
 * Sets the response status to 502 and the status text to "Bad Gateway".
 *
 * @param body The value to use as the response body.
 * @param headers Headers to include on the response.
 * @param cookies Cookies to include in the response.
 * @return A ResponseDTO with status 502, statusText "Bad Gateway", and the provided body, headers, and cookies.
 */
inline fun <reified T> badGateway(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 502
        this.statusText = "Bad Gateway"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 503 Service Unavailable response using the provided body.
 *
 * @param body The response payload.
 * @param headers Header name/value pairs to set on the response.
 * @param cookies Cookies to include as `Set-Cookie` headers.
 * @return A ResponseDTO with status 503 and statusText "Service Unavailable" containing the provided body, headers, and cookies.
 */
inline fun <reified T> serviceUnavailable(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 503
        this.statusText = "Service Unavailable"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP 504 (Gateway Timeout) response containing the provided body.
 *
 * @param body The response payload.
 * @param headers HTTP headers to include in the response.
 * @param cookies Cookies to include as `Set-Cookie` headers.
 * @return A ResponseDTO with status 504, status text "Gateway Timeout", and the supplied body, headers, and cookies.
 */
inline fun <reified T> gatewayTimeout(
    body: T,
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        this.status = 504
        this.statusText = "Gateway Timeout"
        this.body = body
        this.headers = headers
        this.cookies.addAll(cookies)
    }

/**
 * Create an HTTP response that initiates a file download for the given file.
 *
 * The response uses status 200 ("OK"), sets `Content-Disposition` to `attachment` with the file's name,
 * and includes a `Content-Type` header from `contentType` if provided or guessed from the file otherwise.
 * The file's bytes are read into memory and used as the response body.
 *
 * @param file The file to be sent as the downloadable payload.
 * @param contentType Optional explicit MIME type to set for the `Content-Type` header; when `null` the MIME type is guessed from the file.
 * @param cookies Additional cookies to include in the response.
 * @return A ResponseDTO whose body contains the file's bytes and whose headers and status are set to deliver the file as an attachment.
 */
fun fileDownload(
    file: File,
    contentType: String? = null,
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse<ByteArray> {
        status = 200
        statusText = "OK"
        headers {
            put("Content-Type", contentType ?: guessContentType(file))
            put("Content-Disposition", "attachment; filename=\"${file.name}\"")
        }
        body = file.readBytes()
        this.cookies.addAll(cookies)
    }

/**
 * Determine the MIME content type for the provided file.
 *
 * Uses the platform probe for file types first, then falls back to a name-based heuristic, and finally returns
 * "application/octet-stream" when the type cannot be determined.
 *
 * @return The MIME type string for the file, or `"application/octet-stream"` if unknown.
 */
fun guessContentType(file: File): String =
    Files.probeContentType(file.toPath())
        ?: URLConnection.guessContentTypeFromName(file.name)
        ?: "application/octet-stream"

/**
 * Produce an HTTP 418 "I'm a teapot" response.
 *
 * @param headers Headers to include in the response; replaces the default header map.
 * @param cookies Cookies to include in the response.
 * @return A ResponseDTO with status 418 ("I'm a teapot"), body "may be short and stout", and the provided headers and cookies.
 */
fun teapot(
    headers: Headers = mutableMapOf(),
    cookies: List<Cookie> = emptyList(),
): ResponseDTO =
    buildResponse {
        status = 418
        statusText = "I'm a teapot"
        this.headers = headers
        this.cookies.addAll(cookies)
        body = "may be short and stout"
    }
