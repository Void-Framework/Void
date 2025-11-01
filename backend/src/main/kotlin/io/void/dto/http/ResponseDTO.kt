package io.void.dto.http

import java.io.File
import java.io.OutputStream
import java.io.PrintWriter
import java.net.URLConnection
import java.nio.file.Files
import kotlin.reflect.full.memberProperties

/** JSON key/value map used by legacy JSON builder utilities. */
typealias JSON = Map<String, Any?>

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
    /** Mutable map of response headers. "Content-Length" will be added if missing during write. */
    var headers = mutableMapOf<String, String>()

    /**
     * Back-reference to the originating [RequestDTO].
     * Set internally by the router so middleware can correlate requests and responses.
     */
    internal lateinit var _request: RequestDTO

    /** Public accessor for the originating request; valid after routing sets it. */
    val request: RequestDTO
        get() = _request

    /** Factory and helper functions for building JSON strings (legacy) and typed ResponseDTOs. */
    companion object {
        private fun generateJson(keyAndValue: Map<String, Any?>) {
            keyAndValue.forEach {
                generateJson(it.key, it.value)
            }
        }

        private fun generateJson(
            key: String,
            value: Any?,
        ): String? {
            when (value) {
                null -> return "\"$key\":null,"
                is Boolean, is Number -> return "\"$key\":$value,"
                is String -> return "\"$key\":\"$value\","
                is Array<*>, is Iterable<*> -> {
                    val items =
                        when (value) {
                            is Array<*> -> value.toList()
                            is Collection<*> -> value.toList()
                            else -> emptyList()
                        }
                    val arrayBuilder = StringBuilder("[")
                    items.forEach {
                        when (it) {
                            is Number, is Boolean -> arrayBuilder.append("$it,")
                            is String -> arrayBuilder.append("\"$it\",")
                            else -> {
                                val nestedValue = generateJson("", it)
                                if (nestedValue != null) {
                                    val editedValueForList = nestedValue.substring(3)
                                    arrayBuilder.append("${editedValueForList.substringBeforeLast(',')},")
                                }
                            }
                        }
                    }
                    if (items.isNotEmpty()) {
                        arrayBuilder.setLength(arrayBuilder.length - 1)
                    }
                    arrayBuilder.append("]")
                    return ("\"$key\":$arrayBuilder,")
                }

                is Map<*, *> -> {
                    val mapBuilder = StringBuilder("{")

                    value.forEach { (mapKey, mapValue) ->
                        if (mapKey is String) {
                            val nestedValue = generateJson(mapKey, mapValue)
                            if (nestedValue != null) {
                                mapBuilder.append(nestedValue)
                            }
                        } else {
                            throw UnsupportedOperationException("Map keys must be strings.")
                        }
                    }
                    if (value.isNotEmpty()) {
                        mapBuilder.setLength(mapBuilder.length - 1)
                    }
                    mapBuilder.append("}")
                    return "\"$key\":$mapBuilder,"
                }

                else -> {
                    return generateObjectJson(key, value)
                }
            }
        }

        private fun generateObjectJson(
            key: String,
            value: Any,
        ): String {
            val objectBuilder = StringBuilder("\"$key\":{")
            objectBuilder.append(
                "${
                    generateJson(
                        value::class.memberProperties.associate {
                            it.name to
                                it.getter.call(
                                    value,
                                )
                        },
                    )
                }}",
            )
            return objectBuilder.toString()
        }

        @Deprecated("Builder is broken", ReplaceWith("JSONDTO"), DeprecationLevel.WARNING)
        fun json(
            entries: JSON,
            statusInt: Int,
            statusMessage: String,
        ): ResponseDTO {
            val jsonBuilder = StringBuilder("{")

            entries.forEach { (key, value) ->
                jsonBuilder.append(generateJson(key, value))
            }
            if (entries.isNotEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length - 1)
            }

            jsonBuilder.append("}")

            return buildResponse {
                status = statusInt
                statusText = statusMessage
                headers {
                    put("Content-Type", "application/json")
                }
                body = jsonBuilder.toString()
            }
        }
    }

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
    var body: T
}

/** Builder for string-based HTTP responses. */
class StringResponseBuilder : ResponseBuilder<String> {
    override var status: Int = 200
    override var statusText: String = "All is well!"
    override var headers: MutableMap<String, String> = mutableMapOf()
    override var body: String = ""

    fun build(): ResponseDTO =
        ResponseDTO(status, statusText, ResponseBody.StringBody(body)).apply {
            headers =
                this@StringResponseBuilder.headers
        }
}

/** Builder for binary (ByteArray) HTTP responses. */
class ByteResponseBuilder : ResponseBuilder<ByteArray> {
    override var status: Int = 200
    override var statusText: String = "All is well!"
    override var headers: MutableMap<String, String> = mutableMapOf()
    override var body: ByteArray = ByteArray(1)

    fun build(): ResponseDTO =
        ResponseDTO(status, statusText, ResponseBody.ByteArrayBody(body)).apply {
            headers =
                this@ByteResponseBuilder.headers
        }
}

/**
 * Builds a [ResponseDTO] using a type-safe builder for either String or ByteArray bodies.
 * The generic [T] determines which underlying builder is used.
 */
inline fun <reified T> buildResponse(builder: ResponseBuilder<T>.() -> Unit): ResponseDTO {
    val build: ResponseBuilder<T> =
        when (T::class) {
            String::class -> StringResponseBuilder() as ResponseBuilder<T>
            ByteArray::class -> ByteResponseBuilder() as ResponseBuilder<T>
            else -> throw IllegalArgumentException("Unsupported response type: ${T::class}")
        }
    build.builder()
    return when (build) {
        is StringResponseBuilder -> build.build()
        is ByteResponseBuilder -> build.build()
        else -> throw IllegalStateException("Unknown builder type")
    }
}

/** Applies header mutations within the response builder DSL. */
fun ResponseBuilder<*>.headers(block: MutableMap<String, String>.() -> Unit) {
    headers.block()
}

/**
 * Writes the given [response] to this [OutputStream] as an HTTP/1.x message with the provided [version].
 * Ensures Content-Length is present and streams either text or binary bodies appropriately.
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
            is ResponseBody.StringBody ->
                response["Content-Length"] =
                    responseBody.body
                        .toByteArray()
                        .size
                        .toString()

            else -> response["Content-Length"] = (responseBody.body as ByteArray).size.toString()
        }
    }

    for ((key, value) in response.headers.entries) {
        writer.println("$key: $value")
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

fun emptyResponse(): ResponseDTO = buildResponse<String> { }

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

inline fun <reified T> ok(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 200
        this.statusText = "OK"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> created(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 201
        this.statusText = "Created"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> accepted(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 202
        this.statusText = "Accepted"
        this.body = body
        this.headers = headers
    }

fun noContent(headers: Headers): ResponseDTO =
    buildResponse<String> {
        this.status = 204
        this.statusText = "No Content"
        this.body = ""
        this.headers = headers
    }

fun redirect(
    location: String,
    permanent: Boolean = false,
): ResponseDTO =
    buildResponse<String> {
        this.status = if (permanent) 301 else 302
        this.statusText = if (permanent) "Moved Permanently" else "Found"
        this.headers = mutableMapOf("Location" to location)
        this.body = ""
    }

fun temporaryRedirect(location: String): ResponseDTO =
    buildResponse<String> {
        this.status = 307
        this.statusText = "Temporary Redirect"
        this.headers = mutableMapOf("Location" to location)
        this.body = ""
    }

fun permanentRedirect(location: String): ResponseDTO =
    buildResponse<String> {
        this.status = 308
        this.statusText = "Permanent Redirect"
        this.headers = mutableMapOf("Location" to location)
        this.body = ""
    }

inline fun <reified T> badRequest(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 400
        this.statusText = "Bad Request"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> unauthorized(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 401
        this.statusText = "Unauthorized"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> forbidden(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 403
        this.statusText = "Forbidden"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> notFound(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 404
        this.statusText = "Not Found"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> conflict(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 409
        this.statusText = "Conflict"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> tooManyRequests(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 429
        this.statusText = "Too Many Requests"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> internalServerError(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 500
        this.statusText = "Internal Server Error"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> notImplemented(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 501
        this.statusText = "Not Implemented"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> badGateway(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 502
        this.statusText = "Bad Gateway"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> serviceUnavailable(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 503
        this.statusText = "Service Unavailable"
        this.body = body
        this.headers = headers
    }

inline fun <reified T> gatewayTimeout(
    body: T,
    headers: Headers = mutableMapOf(),
): ResponseDTO =
    buildResponse {
        this.status = 504
        this.statusText = "Gateway Timeout"
        this.body = body
        this.headers = headers
    }

fun fileDownload(file: File, contentType: String? = null): ResponseDTO = buildResponse {
    status = 200
    statusText = "OK"
    headers {
        put("Content-Type", contentType ?: guessContentType(file))
        put("Content-Disposition", "attachment; filename=\"${file.name}\"")
    }
    body = file.readBytes() // or InputStream for streaming
}

fun guessContentType(file: File): String = Files.probeContentType(file.toPath())
    ?: URLConnection.guessContentTypeFromName(file.name)
    ?: "application/octet-stream"
