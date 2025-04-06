package io.void.js.keywords

data class Const<T>(val value: T, val name: String): Keyword {

    override var jsReturn: String = "const $name = ${if (value is Keyword) {
        value.render()
    } else {
        "$value"
    }
    }"

    override fun render(): String {
        return jsReturn
    }
}