package io.void.js.keywords.controlflow

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

data class If(
    val condition: String,
    val _body: JavaScript.(Function) -> Unit,
    val js: JavaScript
): Function(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    init {
        _body(js, this)
    }

    private fun renderBlock(statements: List<Keyword>): String {
        return statements.joinToString(";") { 
            val rendered = it.render()
            if (!rendered.endsWith(";")) "$rendered;" else rendered
        }
    }

    override var jsReturn: String = "if($condition) {${renderBlock(children)}}"

    override fun render(): String {
        return jsReturn
    }

    fun ElseIf(condition: String, body: JavaScript.(Function) -> Unit): If {
        children.clear()
        body(js, this)
        jsReturn += "else if ($condition) {${renderBlock(children)}}"
        return this
    }

    fun Else(body: JavaScript.(Function) -> Unit): If {
        children.clear()
        body(js, this)
        jsReturn += "else {${renderBlock(children)}}"
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