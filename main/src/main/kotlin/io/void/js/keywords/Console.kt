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
    fun table(list: JsValue<*>, collums: JsValue<Int>? = null): Reference<Console> {
        jsReturn += ".table($list${if (collums != null) ", $collums" else ""})"
        return this.refer()
    }
    fun warn(message: JsValue<*>): Reference<Console> {
        jsReturn += ".warn($message)"
        return this.refer()
    }
    fun time(label: JsValue<String>? = null): Reference<Console> {
        jsReturn += ".time(${if (label != null) "$label" else ""})"
        return this.refer()
    }
    fun timeEnd(label: JsValue<String>? = null): Reference<Console> {
        jsReturn += ".timeEnd(${if (label != null) "$label" else ""})"
        return this.refer()
    }
    fun timeLog(label: JsValue<String>? = null): Reference<Console> {
        jsReturn += ".timeLog(${if (label != null) "$label" else ""})"
        return this.refer()
    }
}

fun JavaScript.log(message: JsValue<*>, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.log(message)
}
fun JavaScript.error(message: JsValue<*>, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.error(message)
}
fun JavaScript.info(message: JsValue<*>, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.info(message)
}
fun JavaScript.table(list: JsValue<*>, collums: JsValue<Int>? = null, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.table(list, collums)
}
fun JavaScript.warn(message: JsValue<*>, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.warn(message)
}
fun JavaScript.time(label: JsValue<String>? = null, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.time(label)
}
fun JavaScript.timeEnd(label: JsValue<String>? = null, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.timeEnd(label)
}
fun JavaScript.timeLog(label: JsValue<String>? = null, console: JsValue<*>? = null) {
    val console = Console(console)
    children.add(console)
    console.timeLog(label)
}