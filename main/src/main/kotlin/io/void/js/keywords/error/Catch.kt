package io.void.js.keywords.error

import io.void.js.JavaScript
import io.void.js.keywords.Function

data class CatchFunction(
    val _body: JavaScript.(Function<Nothing>) -> Unit,
    val js: JavaScript,
    val errorName: String
): Function<Nothing>(
    name = "",
    body = _body,
    arguments = listOf(errorName)
) {
    init {
        body(js, this)
    }

    override var jsReturn: String = children.joinToString(";") { it.render() }

    override fun render(): String {
        return jsReturn
    }
}

data class Catch(
    val _body: CatchFunction,
    val js: JavaScript
): Function<Nothing>(
    name = "",
    body = _body._body
) {
    init {
        body(js, this)
    }

    override fun render(): String {
        return "catch {${_body.render()}}"
    }
}