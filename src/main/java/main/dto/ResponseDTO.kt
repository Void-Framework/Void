package main.java.main.DTO

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Primitive

typealias JSON = Map<String, Any?>
typealias Headers = Map<String, String>

data class ResponseDTO(var status: Int, var statusText: String, var headers: Headers, var body: String) {

    companion object {
        private fun generateJson(keyAndValue: Map<String, Any?>) {
            keyAndValue.forEach {
                generateJson(it.key, it.value)
            }
        }

        private fun generateJson(key: String, value: Any?): String? {
            when (value) {
                is Boolean, is Number -> return "\"$key\":$value,"
                is String -> return "\"$key\":\"$value\","
                is Array<*> -> {
                    val arrayBuilder = StringBuilder("[")
                    value.forEach {
                        when (it) {
                            is Number, is Boolean -> arrayBuilder.append("$it,")
                            is String -> arrayBuilder.append("\"$it\",")
                            else -> arrayBuilder.append(generateJson(key, value))
                        }
                    }
                    if (value.isNotEmpty()) {
                        arrayBuilder.setLength(arrayBuilder.length - 1)
                    }
                    arrayBuilder.append("]")
                    return ("\"$key\":$arrayBuilder,")
                }
                is Map<*, *> -> {
                    val mapBuilder = StringBuilder("\"$key\":{")
                    value.forEach {
                        if (it.key is String) {
                            if (it.value is String) {
                                mapBuilder.append("\"$it.key\":\"$it.value\",")
                            } else if (it.value is Boolean || it.value is Number || it.value == null) {
                                mapBuilder.append("\"$it.key\":$it.value,")
                            } else {
                                mapBuilder.append("${ it.value?.let { it1 -> generateObjectJson(it.key as String, it1) } },")
                            }
                        } else {
                            throw UnsupportedOperationException("Names of parameters in a json map can only be strings")
                        }
                    }
                    if (value.isNotEmpty()) {
                        mapBuilder.setLength(mapBuilder.length - 1)
                    }
                    mapBuilder.append("}")
                }
                is Collection<*> -> {
                    val arrayBuilder = StringBuilder("[")
                    value.forEach {
                        when (it) {
                            is Number, is Boolean -> arrayBuilder.append("$it,")
                            is String -> arrayBuilder.append("\"$it\",")
                            else -> arrayBuilder.append(generateJson(key, value))
                        }
                    }
                    if (value.isNotEmpty()) {
                        arrayBuilder.setLength(arrayBuilder.length - 1)
                    }
                    arrayBuilder.append("]")
                    return ("\"$key\":$arrayBuilder,")
                }
                else -> {
                    if (value != null) {
                        return generateObjectJson(key, value)
                    } else {
                        return "\"$key\":$value,"
                    }
                }
            }
            return null
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

        fun json(entries: JSON, statusInt: Int, statusMessage: String): ResponseDTO {
            val jsonBuilder = StringBuilder("{")

            entries.forEach { (key, value) ->
                jsonBuilder.append(value?.let { generateJson(key, it) })
            }

            if (entries.isNotEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length - 1) // Remove last comma
            }
            jsonBuilder.append("}")

            return ResponseDTO(
                status = statusInt,
                statusText = statusMessage,
                headers = mapOf("Content-Type" to "application/json"),
                body = jsonBuilder.toString(),
            )
        }
    }
}