package io.void.js.keywords.error

import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.JavaScript

data class Finally(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
): Function<Nothing>(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    override var jsReturn: String = "finally {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}