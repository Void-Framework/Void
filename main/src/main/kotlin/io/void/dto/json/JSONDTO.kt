package io.void.dto.json

import kotlin.reflect.full.memberProperties

typealias JSONObject = Map<String, Any?>

data class JSONDTO(val obj: JSONObject) {
    private val json = StringBuilder("{")

    private fun handleObject(value: Any): String {
        if (value is Map<*, *>) {
            return value.joinToString(", ") { key, mValue ->
                handleItem(key.toString(), mValue)
            }
        } else {
            val clazz = value::class
            if (!clazz.isData) throw IllegalArgumentException()
            return clazz.memberProperties.joinToString(", ") {
                handleItem(it.name, it.getter.call(value))
            }
        }
    }

    fun turnToJson(): String {
        obj.forEach { (key, value) ->
            json.append("${handleItem(key, value)},")
        }
        if (obj.isNotEmpty()) {
            json.setLength(json.length - 1)
            json.append("}")
        }
        return json.toString()
    }

    private fun handleItem(key: String, value: Any?): String {
        return when (value) {
            null -> "\"$key\": null"
            is String, Char -> "\"$key\": \"$value\""
            is Number, Boolean -> "\"$key\": $value"
            is Map<*, *> -> "\"$key\": {${handleObject(value)}}"
            is Iterable<*>, is Array<*> -> "\"$key\": [${handleLists(value)}]"
            else -> "\"$key\": {${handleObject(value)}}"
        }
    }
    private fun handleListItem(value: Any?): String {
        return when (value) {
            null -> "null"
            is String, Char -> "\"$value\""
            is Number, Boolean -> "$value"
            is Map<*, *> -> "{${handleObject(value)}}"
            is Iterable<*>, is Array<*> -> "[${handleLists(value)}]"
            else -> "{${handleObject(value)}}"
        }
    }

    private fun handleLists(value: Any): String {
        val newValue = when (value) {
            is Array<*> -> value.toList()
            is Iterable<*> -> value
            else -> emptyList()
        }

        return newValue.joinToString(", ") {
            handleListItem(it)
        }
    }
}

private fun <K, V> Map<K, V>.joinToString(separator: CharSequence, transform: ((K, V) -> CharSequence)): String {
    val builder = StringBuilder("")
    forEach { (key, value) ->
        builder.append("${transform(key, value)}$separator")
    }
    if (isNotEmpty()) {
        builder.setLength(builder.length - separator.length)
    }
    return builder.toString()
}