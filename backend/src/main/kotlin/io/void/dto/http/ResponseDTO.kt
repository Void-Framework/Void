package io.void.dto.http

import java.io.OutputStream
import java.io.PrintWriter
import kotlin.reflect.full.memberProperties

typealias Headers = Map<String, String>
typealias JSON = Map<String, Any?>

data class ResponseDTO(
    val status: Int,
    val statusText: String,
    val body: ResponseBody<*>,
) {
    private val _headers = mutableMapOf<String, String>()
    var headers: Headers
        get() = _headers
        set(value) {
            _headers.putAll(value)
        }

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
    ) = _headers.put(headerName, headerValue)
}

interface ResponseBuilder<T> {
    var status: Int
    var statusText: String
    var headers: MutableMap<String, String>
    var body: T
}

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

fun ResponseBuilder<*>.headers(block: MutableMap<String, String>.() -> Unit) {
    headers.block()
}

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
    writer.println(response.body.body)

    writer.flush()
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
