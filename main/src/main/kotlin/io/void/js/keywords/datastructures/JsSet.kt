package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword

data class JsSet<T>(val baseList: JsValue<T>): JsDatastructure {

    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "new Set($baseList)"
        return this
    }

    fun add(value: JsValue<T>): JsSet<T> {
        jsReturn += ".add($value)"
        return this@JsSet
    }
    fun has(value: JsValue<T>): JsSet<T> {
        jsReturn += ".has($value)"
        return this
    }
    fun delete(value: JsValue<T>): JsSet<T> {
        jsReturn += ".delete($value)"
        return this
    }
    fun clear(): JsSet<T> {
        jsReturn += ".clear()"
        return this
    }
    fun size(): JsSet<T> {
        jsReturn += ".size"
        return this
    }
}

inline fun <reified T> JavaScript.set(baseList: JsValue<T>): JsSet<T> {
    val set = JsSet(baseList = baseList).initialize()
    children.add(set)
    return set as JsSet<T>
}