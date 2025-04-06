package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class JsSet<T>(val baseList: JsList<T>): Keyword {

    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    fun initialize(): JsSet<T> {
        jsReturn = "new Set(${baseList.render()})"
        return this
    }

    inner class Actions {
        fun add(value: T): JsSet<T> {
            jsReturn += ".add($value)"
            return this@JsSet
        }
        fun has(value: T): JsSet<T> {
            jsReturn += ".has($value)"
            return this@JsSet
        }
        fun delete(value: T): JsSet<T> {
            jsReturn += ".delete($value)"
            return this@JsSet
        }
        fun clear(): JsSet<T> {
            jsReturn += ".clear()"
            return this@JsSet
        }
        fun size(): JsSet<T> {
            jsReturn += ".size"
            return this@JsSet
        }
    }
}

inline fun <reified T> JavaScript.set(baseList: JsList<T>): JsSet<T> {
    val set = JsSet(baseList = baseList).initialize()
    children.add(set)
    return set
}