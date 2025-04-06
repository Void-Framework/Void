package io.void.js.keywords

class Let<T>(val value: T, val name: String): Keyword {

    override var jsReturn: String = "let $name = $value"

    override fun render(): String {
        return jsReturn
    }

    inner class Setter(newValue: T): Keyword {

        override var jsReturn: String = "$name = $newValue"

        override fun render(): String {
            return jsReturn
        }

    }
}