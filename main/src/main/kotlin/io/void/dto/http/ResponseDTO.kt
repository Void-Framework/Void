package io.void.dto.http

import java.io.OutputStream
import java.io.PrintWriter
import kotlin.reflect.full.memberProperties

typealias JSON = MutableMap<String, Any?>
typealias Headers = MutableMap<String, String>

data class ResponseDTO(var status: Int, var statusText: String, var headers: Headers, var body: String) {

    companion object {
        private fun generateJson(keyAndValue: Map<String, Any?>) {
            keyAndValue.forEach {
                generateJson(it.key, it.value)
            }
        }

        fun build(response: ResponseDTO, outputStream: OutputStream)  {
            val writer = PrintWriter(outputStream, true)
            writer.println("HTTP/1.1 ${response.status} ${response.statusText}")

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