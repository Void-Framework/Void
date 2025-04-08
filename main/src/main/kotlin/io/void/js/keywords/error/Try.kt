package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.datastructures.Void

data class Try(
    val _body: JavaScript.(Function) -> Unit,
    val js: JavaScript
): Function(
    name = "",
    body = _body
) {
    init {
        body(js, this)
    }

    override fun render(): String {
        return "try {${children.joinToString(";") { it.render() }}}"
    }

    fun catch(catchClause: Catch): Void {
        jsReturn += catchClause.render()
        return Void()
    }
}

fun JavaScript.Try(body: JavaScript.(Function) -> Unit): Try {
    val Try = Try(
        _body = body,
        js = this
    )
    children.add(Try)
    return Try
}