package io.void.js.keywords.controlflow

import io.void.js.JavaScript
import io.void.js.keywords.Function

data class If(val condition: String, val _body: JavaScript.(Function) -> Unit, val js: JavaScript): Function(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    init {
        _body(js, this)
    }

    override var jsReturn: String = "if($condition) {${children.joinToString(";") { it.render() }}}"

    fun ElseIf(condition: String, body: JavaScript.(Function) -> Unit): If {
        body(js, this)
        jsReturn += "else if ($condition) {${children.joinToString(";") { it.render() }}}"
        return this
    }

    fun Else(body: JavaScript.(Function) -> Unit): If {
        body(js, this)
        jsReturn += "else {${children.joinToString(";") { it.render() }}}"
        return this
    }
}

fun JavaScript.If(condition: String, body: JavaScript.(Function) -> Unit): If {
    val conditional = If(
        condition = condition,
        _body = body,
        js = this
    )
    children.add(conditional)
    return conditional
}