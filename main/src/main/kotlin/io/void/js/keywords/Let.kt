package io.void.js.keywords

class Let<T>(val value: T, val name: String): Keyword {

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

        override var jsReturn: String = "$name = ${if (value is Keyword) {
            value.render()
        } else {
            "$value"
        }
        }"

        override fun render(): String {
            return jsReturn
        }

    }
}