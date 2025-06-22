package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.asJsValue
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.refer

data class JsSet<T>(val baseList: JsValue<T>): JsDatastructure {

    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "new Set($baseList)"
        return this
    }
    fun emptySet(): JsSet<T> {
        jsReturn = "new Set()"
        return this
    }

    fun add(value: JsValue<T>): JsSet<T> {
        jsReturn += ".add($value)"
        return this
    }
    fun has(value: JsValue<T>): JsValue<Boolean> {
        jsReturn += ".has($value)"
        return true.asJsValue()
    }
    fun delete(value: JsValue<T>): JsValue<Boolean> {
        jsReturn += ".delete($value)"
        return true.asJsValue()
    }
    fun clear(): Reference<JsSet<T>> {
        jsReturn += ".clear()"
        return this.refer()
    }
    fun size(): JsValue<Int> {
        jsReturn += ".size"
        return 0.asJsValue()
    }
}

inline fun <reified T> JavaScript.set(baseList: JsValue<T>): JsSet<T> {
    val set = JsSet(baseList = baseList).initialize()
    children.add(set)
    return set as JsSet<T>
}
inline fun <reified T> JavaScript.emptySet(): JsSet<T> {
    val set = JsSet(baseList = emptyJsValue()).emptySet()
    children.add(set)
    return set as JsSet<T>
}
inline fun <reified T> Set<T>.asJsSet(): JsSet<T> {
    val set = JsSet(this.asJsValue())
    return set as JsSet<T>
}