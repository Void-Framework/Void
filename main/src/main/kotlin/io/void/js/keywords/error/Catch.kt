package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.Function
import io.void.js.FunctionVariable

data class CatchFunction(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit,
    val errorName: String
): Function<Nothing>(
    name = "",
    body = _body,
    arguments = listOf(errorName)
) {

    override var jsReturn: String = children.joinToString(";") { it.render() }

    override fun render(): String {
        return jsReturn
    }
}

data class Catch(
    val _body: CatchFunction
): Function<Nothing>(
    name = "",
    body = _body._body
) {

    override fun render(): String {
        return "catch (${_body.errorName}) {${_body.render()}}"
    }
}