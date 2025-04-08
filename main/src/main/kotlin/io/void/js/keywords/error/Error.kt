package io.void.js.keywords.error

import io.void.js.keywords.Keyword
import io.void.js.keywords.string.TemplateString

data class Error(
    val message: String,
    val cause: String
): Keyword {

    override var jsReturn: String = "new Error(${if (TemplateString.isTemplateString(message)) {
        "`$message`"
    } else {
        message
    }
    }$cause)"

    override fun render(): String {
        return jsReturn
    }
}