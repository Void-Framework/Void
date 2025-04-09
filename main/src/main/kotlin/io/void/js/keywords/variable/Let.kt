package io.void.js.keywords.variable

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class Let<T>(override val value: T?, override val name: String): Variable<T> {

    override var jsReturn: String = "let $name = ${if (value is Keyword) {
        value.render()
    } else {
        "$value"
    }
    }"

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