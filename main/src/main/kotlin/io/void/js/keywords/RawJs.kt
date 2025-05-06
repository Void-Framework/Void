package io.void.js.keywords

import io.void.js.JavaScript

data class RawJs(
    val operation: String
): Keyword {

    override var jsReturn: String = operation
    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.raw(call: String): RawJs {
    val rawJs = RawJs(operation = call)
    children.add(rawJs)
    return rawJs
}