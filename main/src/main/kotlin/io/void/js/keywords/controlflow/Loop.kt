package io.void.js.keywords.controlflow

import io.void.js.JavaScript
import io.void.js.keywords.Function

data class While(
    val condition: String,
    val _body: JavaScript.(Function) -> Unit,
    val js: JavaScript
): Function(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    init {
        body(js, this)
    }

    override var jsReturn: String = "while($condition) {"

    override fun render(): String {
        return "$jsReturn${children.joinToString(";") { it.render() }}}"
    }
}

data class For(
    val condition: String,
    val _body: JavaScript.(Function) -> Unit,
    val js: JavaScript
): Function(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    init {
        body(js, this)
    }

    override var jsReturn: String = "for($condition) {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.While(condition: String, body: JavaScript.(Function) -> Unit): While {
    val While = While(
        condition = condition,
        _body = body,
        js = this
    )
    children.add(While)
    return While
}

fun JavaScript.For(condition: String, body: JavaScript.(Function) -> Unit): For {
    val For = For(
        condition = condition,
        _body = body,
        js = this
    )
    children.add(For)
    return For
}