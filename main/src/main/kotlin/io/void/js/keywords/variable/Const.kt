package io.void.js.keywords.variable

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class Const<T>(val value: T, val name: String): Variable {

    override var jsReturn: String = "const $name = $value"

    override fun render(): String {
        return jsReturn
    }
}

inline fun <reified T> JavaScript.const(value: T, name: String): Const<T> {
    val constant = Const(
        value = value,
        name = name
    )
    children.add(constant)
    return constant
}