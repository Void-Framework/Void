package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.Function
import io.void.js.FunctionVariable

data class Lambda<T>(
    val _arguments: List<String>,
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit,
): Function<T>(
    name = "",
    body = _body,
    arguments = _arguments
) {
    override var jsReturn: String = "(${_arguments.joinToString(", ")} => {${children.joinToString(";") { it.render() }}})"

    override fun render(): String {
        return jsReturn
    }

    override fun run(arguments: JsValue<*>): String {
        return "${render()}($arguments)"
    }
}

inline fun <reified T> JavaScript.Lambda(noinline body: JavaScript.(List<FunctionVariable<*>>) -> Unit, arguments: List<String>): Lambda<T> {
    val lambda = Lambda<T>(
        _body = body,
        _arguments = arguments
    )
    children.add(lambda)
    return lambda
}