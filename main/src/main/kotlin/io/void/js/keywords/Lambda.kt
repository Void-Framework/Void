package io.void.js.keywords

import io.void.js.JavaScript

data class Lambda(
    val _arguments: List<String>,
    val _body: JavaScript.(Function) -> Unit,
    val js: JavaScript
): Function(
    name = "",
    body = _body,
    arguments = _arguments
) {

    init {
        body(js, this)
    }

    override var jsReturn: String = "(${_arguments.joinToString(", ")} => {${children.joinToString(";") { it.render() }}})"

    override fun render(): String {
        return jsReturn
    }

    override fun run(arguments: JsValue<*>): String {
        return "${render()}($arguments)"
    }
}

fun JavaScript.Lambda(body: JavaScript.(Function) -> Unit, arguments: List<String>): Lambda {
    val lambda = Lambda(
        _body = body,
        _arguments = arguments,
        js = this
    )
    children.add(lambda)
    return lambda
}