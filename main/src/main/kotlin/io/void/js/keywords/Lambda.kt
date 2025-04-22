package io.void.js.keywords

import io.void.js.JavaScript

data class Lambda<T>(
    val _arguments: List<String>,
    val _body: JavaScript.(Function<T>) -> Unit,
    val js: JavaScript
): Function<T>(
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

inline fun <reified T> JavaScript.Lambda(noinline body: JavaScript.(Function<T>) -> Unit, arguments: List<String>): Lambda<T> {
    val lambda = Lambda(
        _body = body,
        _arguments = arguments,
        js = this
    )
    children.add(lambda)
    return lambda
}