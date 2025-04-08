package io.void.js.keywords

import io.void.js.JavaScript

data class InlineCall(
    val operation: String
): Keyword {

    override var jsReturn: String = operation
    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.InlineCall(call: String): InlineCall {
    val inlineCall = InlineCall(operation = call)
    children.add(inlineCall)
    return inlineCall
}