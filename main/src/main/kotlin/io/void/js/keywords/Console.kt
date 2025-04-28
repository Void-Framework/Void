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
    fun error(message: JsValue<*>): Reference<Console> {
        jsReturn += ".error($message)"
        return this.refer()
    }
    fun info(message: JsValue<*>): Reference<Console> {
        jsReturn += ".info($message)"
        return this.refer()
    }
    fun log(message: JsValue<*>): Reference<Console> {
        jsReturn += ".log($message)"
        return this.refer()
    }
    fun table(list: JsValue<*>): Reference<Console> {
        jsReturn += ".table($list)"
        return this.refer()
    }
    fun warn(message: JsValue<*>): Reference<Console> {
        jsReturn += ".warn($message)"
        return this.refer()
    }
}

fun JavaScript.console(): Console {
    val console = Console()
    children.add(console)
    return console
}