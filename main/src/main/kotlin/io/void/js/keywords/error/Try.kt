package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

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
}