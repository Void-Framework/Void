package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

class Console(console: JsValue<*>? = null): Keyword {

    override var jsReturn: String = console?.toJs() ?: "console"

    override fun render(): String {
        return jsReturn
    }
    fun error(message: JsValue<*>): Void {
        jsReturn += ".error($message)"
        return Void()
    }
    fun info(message: JsValue<*>): Void {
        jsReturn += ".info($message)"
        return Void()
    }
    fun log(message: JsValue<*>): Void {
        jsReturn += ".log($message)"
        return Void()
    }
    fun table(list: JsValue<*>): Void {
        jsReturn += ".table($list)"
        return Void()
    }
    fun warn(message: JsValue<*>): Void {
        jsReturn += ".warn($message)"
        return Void()
    }
}

fun JavaScript.console(): Console {
    val console = Console()
    children.add(console)
    return console
}