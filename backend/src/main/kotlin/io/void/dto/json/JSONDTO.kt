package io.void.dto.json

import kotlin.reflect.full.memberProperties

/** Alias for a JSON-like object represented as a map of keys to arbitrary values. */
typealias JSONObject = Map<String, Any?>

/**
 * Simple JSON builder for small objects and data classes.
 *
 * Given a [JSONObject] map, or a Kotlin data class instance nested within values,
 * this utility constructs a JSON string without external dependencies.
 *
 * Notes and limitations:
 * - This is a lightweight formatter meant for simple cases; it does not escape all
 *   possible characters for strict JSON compliance (e.g., embedded quotes or control
 *   characters in strings are not escaped beyond basic interpolation).
 * - When encountering an arbitrary object, it must be a Kotlin data class; otherwise
 *   an [IllegalArgumentException] is thrown.
 * - Collection handling supports Iterables and Arrays recursively.
 */
data class JSONDTO(
    val obj: JSONObject,
) {
    private val json = StringBuilder("{")

    /**
     * Serializes either a Map or a Kotlin data class instance to a JSON object body.
     *
     * @throws IllegalArgumentException if [value] is not a Map and not a data class.
     */
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

    /**
     * Produces the final JSON string for [obj].
     *
     * The builder appends a trailing comma while iterating and removes it at the end
     * when the object is not empty, then closes the object with "}".
     */
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

    /**
     * Renders a single key/value pair into a JSON field.
     *
     * Supported value types: null, primitives (String/Char/Number/Boolean), Map,
     * Iterable/Array, or Kotlin data classes (via reflection of properties).
     */
    private fun handleItem(
        key: String,
        value: Any?,
    ): String =
        when (value) {
            null -> "\"$key\": null"
            is String, Char -> "\"$key\": \"$value\""
            is Number, Boolean -> "\"$key\": $value"
            is Map<*, *> -> "\"$key\": {${handleObject(value)}}"
            is Iterable<*>, is Array<*> -> "\"$key\": [${handleLists(value)}]"
            else -> "\"$key\": {${handleObject(value)}}"
        }

    /** Renders a list/array element value into JSON. */
    private fun handleListItem(value: Any?): String =
        when (value) {
            null -> "null"
            is String, Char -> "\"$value\""
            is Number, Boolean -> "$value"
            is Map<*, *> -> "{${handleObject(value)}}"
            is Iterable<*>, is Array<*> -> "[${handleLists(value)}]"
            else -> "{${handleObject(value)}}"
        }

    /** Serializes an Iterable or Array to a JSON array body. */
    private fun handleLists(value: Any): String {
        val newValue =
            when (value) {
                is Array<*> -> value.toList()
                is Iterable<*> -> value
                else -> emptyList()
            }

        return newValue.joinToString(", ") {
            handleListItem(it)
        }
    }
}

/**
 * Lightweight variant of joinToString for Map that provides both key and value
 * to the [transform] function and handles trailing separator trimming.
 */
private fun <K, V> Map<K, V>.joinToString(
    separator: CharSequence,
    transform: ((K, V) -> CharSequence),
): String {
    val builder = StringBuilder("")
    forEach { (key, value) ->
        builder.append("${transform(key, value)}$separator")
    }
    if (isNotEmpty()) {
        builder.setLength(builder.length - separator.length)
    }
    return builder.toString()
}
