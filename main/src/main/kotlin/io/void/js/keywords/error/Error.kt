package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.keywords.Keyword
import io.void.js.keywords.string.TemplateString

data class Error(
    val message: String,
    val extraData: String
): Keyword {

    override var jsReturn: String = "new Error(${if (TemplateString.isTemplateString(message)) {
        "`$message`"
    } else {
        message
    }
    }$extraData)"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.Error(name: String, extraData: String): Error {
    val error = Error(
        message = name,
        extraData = extraData
    )
    children.add(error)
    return error
}