package io.void.js.keywords.datastructures

import io.void.js.data.DataHandler
import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.JavaScript
import io.void.js.data.randomString
import io.void.js.keywords.Keyword
import io.void.js.keywords.Lambda
import io.void.js.keywords.Reference
import io.void.js.keywords.asJsValue
import io.void.js.keywords.refer

interface JsDatastructure: Keyword {

    fun initialize(): JsDatastructure

    fun forEach(body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Reference<JsDatastructure> {
        jsReturn += ".forEach(${Lambda<Nothing>(
            _arguments = listOf(String.randomString(4)),
            _body = body
        ).render()})"
        return this.refer()
    }
}