package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.keywords.Keyword

data class Try(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
): Function<Nothing>(
    name = "",
    body = _body
) {

    override var jsReturn: String = "try {${children.joinToString(";") { it.render() }}}"

    fun catch(catchClause: Catch): Try {
        jsReturn += catchClause.render()
        return this
    }

    fun finally(finally: Finally): Try {
        jsReturn += finally.render()
        return this
    }

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.Try(body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Try {
    val Try = Try(
        _body = body
    )
    children.add(Try)
    return Try
}