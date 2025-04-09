package io.void.js.keywords.variable

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class Let<T>(val value: T, val name: String): Variable {

    override var jsReturn: String = "let $name = $value"

    override fun render(): String {
        return jsReturn
    }

    inner class Setter(newValue: T): Keyword {

        override var jsReturn: String = "$name = $newValue"

        override fun render(): String {
            return this.jsReturn
        }

    }
}

inline fun <reified T> JavaScript.let(value: T, name: String): Let<T> {
    val let = Let(
        value = value,
        name = name
    )
    children.add(let)
    return let
}