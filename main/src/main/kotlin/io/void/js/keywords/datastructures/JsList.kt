package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.Js
import io.void.js.data.randomString
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Lambda
import io.void.js.keywords.Reference
import io.void.js.keywords.asJsValue
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.refer
import kotlin.collections.List

data class JsList<T>(
    val arguments: JsValue<T>
): JsDatastructure {

    override var jsReturn = ""
    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "[$arguments]"
        return this
    }
    fun emptyList(): JsList<T> {
        jsReturn = "[]"
        return this
    }

    fun push(item: JsValue<T>): JsValue<Int> {
        jsReturn += ".push($item)"
        return 0.asJsValue()
    }
    fun pop(call: (T?) -> Unit): Reference<JsList<T>> {
        jsReturn += ".pop()"
        return applyNullableMethods(call, null, this)
    }
    fun shift(call: (T?) -> Unit): Reference<JsList<T>> {
        jsReturn += ".shift()"
        return applyNullableMethods(call, null, this)
    }
    fun unshift(item: JsValue<T>): JsValue<Int> {
        jsReturn += ".unshift($item)"
        return 0.asJsValue()
    }
    fun splice(index: JsValue<T>, count: JsValue<T>? = null, item: JsValue<*>?): JsList<T> {
        jsReturn += ".splice($index${if (count != null) {
            ", $count"
        } else {
            ""
        }
        }${if (item != null) {
            ", $item"
        } else {
            ""
        }
        })"
        return this
    }
    fun map(body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Reference<JsList<T>> {
        jsReturn += ".map(${Lambda<Nothing>(
            _arguments = listOf(String.randomString(4)),
            _body = body
        ).render()})"
        return this.refer()
    }
    fun filter(body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Reference<JsList<T>> {
        jsReturn += ".filter(${Lambda<Nothing>(
            _arguments = listOf(String.randomString(4)),
            _body = body
        ).render()})"
        return this.refer()
    }
    fun map(body: JsValue<JavaScript.(List<FunctionVariable<*>>) -> Unit>): Reference<JsList<T>> {
        jsReturn += ".map($body)"
        return this.refer()
    }
    fun filter(body: JsValue<JavaScript.(List<FunctionVariable<*>>) -> Unit>): Reference<JsList<T>> {
        jsReturn += ".filter($body)"
        return this.refer()
    }
    fun includes(item: JsValue<*>): JsValue<Boolean> {
        jsReturn += ".includes($item)"
        return true.asJsValue()
    }
    fun get(index: JsValue<Int>, call: (T?) -> Unit): Reference<JsList<T>> {
        jsReturn += "[$index]"
        return applyNullableMethods(call, null, this)
    }
    fun set(index: JsValue<Int>, value: JsValue<T>): Reference<JsList<T>> {
        jsReturn += "[$index] = $value"
        return this.refer()
    }
}

fun <T> JavaScript.jsList(arguments: JsValue<*>): JsList<T> {
    val list = JsList(arguments = arguments)
    children.add(list)
    return list.initialize() as JsList<T>
}
fun <T> JavaScript.emptyJsList(): JsList<T> {
    val list = JsList(arguments = emptyJsValue())
    children.add(list)
    return list.emptyList() as JsList<T>
}
fun <T> List<T>.asJsList(): JsList<T> {
    val list = JsList(arguments = this.asJsValue())
    return list as JsList<T>
}