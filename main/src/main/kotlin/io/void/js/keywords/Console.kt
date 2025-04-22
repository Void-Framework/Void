package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

class Console(console: JsValue<*>? = null): Keyword {

    override var jsReturn: String = console?.toJs() ?: "console"

    override fun render(): String {
        return jsReturn
    }
    fun error(message: JsValue<String>): Console {
        jsReturn += ".error($message)"
        return this
    }
    fun info(message: JsValue<String>): Console {
        jsReturn += ".info($message)"
        return this
    }
    fun log(message: JsValue<String>): Console {
        jsReturn += ".log($message)"
        return this
    }
    fun table(list: JsValue<*>): Console {
        jsReturn += ".table($list)"
        return this
    }
    fun warn(message: JsValue<String>): Console {
        jsReturn += ".warn($message)"
        return this
    }
}

fun JavaScript.console(): Console {
    val console = Console()
    children.add(console)
    return console
}