package io.void.js.data

import io.void.js.JavaScript
import io.void.js.keywords.HTMLElement
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.variable.Const
import io.void.js.keywords.variable.const

class DataHandler: Keyword {

    override var jsReturn: String = "bind"
    override fun render(): String {
        return jsReturn
    }

    fun text(element: JsValue<HTMLElement>, ref: JsValue<DataHolder>) {
        jsReturn += "Text($element, $ref)"
    }
}

fun JavaScript.setData(value: JsValue<*>, name: String): Const<DataHolder> {
    return const(
        name = name,
        value = DataHolder(this).initialize(value)
    )
}

fun String.Companion.randomString(length: Int): String {
    val chars = ('a'..'z') + ('A'..'Z')
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}