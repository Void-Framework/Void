package io.void.dto.http

import io.void.api.method.Method
import java.io.OutputStream
import java.io.PrintWriter
import kotlin.reflect.full.memberProperties

typealias Headers = MutableMap<String, String>
typealias JSON = MutableMap<String, Any?>

data class ResponseDTO(
    val status: Int,
    val statusText: String,
    val headers: Headers,
    val body: String,
) {
    companion object {
        internal fun build(
            response: ResponseDTO,
            outputStream: OutputStream,
            version: Number = 1.1,
        ) {
            val writer = PrintWriter(outputStream, true)
            writer.println("HTTP/$version ${response.status} ${response.statusText}")

            val responseBody = response.body
            if (!response.headers.containsKey("Content-Length")) {
                response.headers["Content-Length"] = responseBody.toByteArray().size.toString()
            }

            for ((key, value) in response.headers.entries) {
                writer.println("$key: $value")
            }
            writer.println()
            writer.println(response.body)

            writer.flush()
        }

        private fun generateJson(keyAndValue: Map<String, Any?>) {
            keyAndValue.forEach {
                generateJson(it.key, it.value)
            }
        }

        private fun generateJson(key: String, value: Any?): String? {
            when (value) {
                null -> return "\"$key\":null,"
                is Boolean, is Number -> return "\"$key\":$value,"
                is String -> return "\"$key\":\"$value\","
                is Array<*>, is Iterable<*> -> {
                    val items = when (value) {
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

        private fun generateObjectJson(key: String, value: Any): String {
            val objectBuilder = StringBuilder("\"$key\":{")
            objectBuilder.append("${
                generateJson(value::class.memberProperties.associate {
                    it.name to it.getter.call(
                        value
                    )
                })
            }}")
            return objectBuilder.toString()
        }

        @Deprecated("Builder is broken", ReplaceWith("JSONDTO"), DeprecationLevel.WARNING)
        fun json(entries: JSON, statusInt: Int, statusMessage: String): ResponseDTO {
            val jsonBuilder = StringBuilder("{")

            entries.forEach { (key, value) ->
                jsonBuilder.append(generateJson(key, value))
            }
            if (entries.isNotEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length - 1)
            }

            jsonBuilder.append("}")

            return ResponseDTO(
                status = statusInt,
                statusText = statusMessage,
                headers = mutableMapOf("Content-Type" to "application/json"),
                body = jsonBuilder.toString(),
            )
        }
    }
}

class ResponseBuilder {
    var status: Int = 200
    var statusText: String = "All is well!"
    val headers: MutableMap<String, String> = mutableMapOf()
    var body: String = ""

    fun build(): ResponseDTO = ResponseDTO(status, statusText, headers, body)
}

fun buildResponse(builder: ResponseBuilder.() -> Unit): ResponseDTO {
    val build = ResponseBuilder()
    build.builder()
    return build.build()
}

fun ResponseBuilder.headers(block: Headers.() -> Unit) {
    headers.block()
}
