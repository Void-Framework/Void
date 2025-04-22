package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

data class Try(
    val _body: JavaScript.(Function<Nothing>) -> Unit,
    val js: JavaScript
): Function<Nothing>(
    name = "",
    body = _body
) {
    init {
        body(js, this)
    }

    override var jsReturn: String = "try {${children.joinToString(";") { it.render() }}}"

    fun catch(catchClause: Catch): Try {
        jsReturn += catchClause.render()
        return this
    }

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.Try(body: JavaScript.(Function<Nothing>) -> Unit): Try {
    val Try = Try(
        _body = body,
        js = this
    )
    children.add(Try)
    return Try
}