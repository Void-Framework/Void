package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.string.TemplateString

data class Error(
    val message: JsValue<*>,
    val extraData: JsValue<*>
): Keyword {

    override var jsReturn: String = "new Error($message,$extraData)"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.Error(name: JsValue<*>, extraData: JsValue<*>): Error {
    val error = Error(
        message = name,
        extraData = extraData
    )
    children.add(error)
    return error
}