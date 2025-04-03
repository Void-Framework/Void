package io.void.js.keywords.controlflow

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

data class Conditionals(val condition: String, val _body: JavaScript.(Function) -> Unit, val js: JavaScript): Function(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    init {
        _body(js, this)
    }

    override var jsReturn: String = "if($condition) {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }

    fun elseIf(condition: String, body: JavaScript.(Function) -> Unit): Conditionals {
        body(js, this)
        jsReturn += "else if ($condition) {${children.joinToString(";") { it.render() }}}"
        return this
    }

    fun Else(body: JavaScript.(Function) -> Unit) {
        body(js, this)
        jsReturn += "else {${children.joinToString(";") { it.render() }}}"
    }
}

fun JavaScript.condition(condition: String, body: JavaScript.(Function) -> Unit): Conditionals {
    val conditional = Conditionals(
        condition = condition,
        _body = body,
        js = this
    )
    children.add(conditional)
    return conditional
}