package io.void.html.attributes

import io.void.html.attributes.exception.UnsupportedTypeException
import java.net.URL

class Attribute {
    var name: AttributeNames = AttributeNames.EMPTY
    private val type: AttributeTypes
        get() = name.dataTypes
    var value: Any? = null

    fun isCorrectValue(): Boolean =
        when (value) {
            null -> type == AttributeTypes.NULL
            is String ->
                when {
                    // Handle fragment identifiers for href and form attributes
                    name == AttributeNames.HREF && (value as String).startsWith("#") -> true
                    name == AttributeNames.FORM && (value as String).startsWith("#") -> true

                    // Handle special numeric attributes that can be strings
                    name == AttributeNames.WIDTH && (value as String).endsWith("px", "em", "rem", "%", "vh", "vw") -> true
                    name == AttributeNames.HEIGHT && (value as String).endsWith("px", "em", "rem", "%", "vh", "vw") -> true

                    // Handle space-separated lists
                    name == AttributeNames.CLASS -> true
                    name == AttributeNames.REL -> true

                    // Handle data-* attributes
                    name == AttributeNames.DATA -> true

                    // Handle comma-separated lists
                    name == AttributeNames.ACCEPT -> true
                    name == AttributeNames.SRCSET -> true

                    // Default string check
                    else -> type == AttributeTypes.STRING
                }
            is Boolean -> type == AttributeTypes.BOOLEAN
            is Int ->
                when {
                    // Handle numeric attributes that can be integers
                    name in
                        listOf(
                            AttributeNames.WIDTH,
                            AttributeNames.HEIGHT,
                            AttributeNames.COLS,
                            AttributeNames.ROWS,
                            AttributeNames.MAXLENGTH,
                            AttributeNames.SIZE,
                            AttributeNames.TABINDEX,
                        )
                    -> true
                    else -> type == AttributeTypes.INTEGER
                }
            is Number ->
                when {
                    // Handle numeric attributes that can be floating point
                    name in
                        listOf(
                            AttributeNames.STEP,
                        )
                    -> true
                    else -> type == AttributeTypes.NUMBER
                }
            is URL ->
                when {
                    // Handle URL attributes
                    name in
                        listOf(
                            AttributeNames.HREF,
                            AttributeNames.SRC,
                            AttributeNames.ACTION,
                            AttributeNames.FORMACTION,
                        )
                    -> true
                    else -> type == AttributeTypes.URL
                }
            else -> throw UnsupportedTypeException()
        }

    private fun String.endsWith(vararg suffixes: String): Boolean = suffixes.any { this.endsWith(it) }
}

fun attribute(func: Attribute.() -> Unit): Attribute {
    val attribute = Attribute()
    attribute.func()
    return attribute
}
