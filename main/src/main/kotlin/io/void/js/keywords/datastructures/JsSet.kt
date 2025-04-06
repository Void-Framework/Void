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
        fun add(value: T) {
            jsReturn += ".add($value)"
        }
        fun has(value: T) {
            jsReturn += ".has($value)"
        }
        fun delete(value: T) {
            jsReturn += ".delete($value)"
        }
        fun clear() {
            jsReturn += ".clear()"
        }
        fun size() {
            jsReturn += ".size"
        }
    }
}

inline fun <reified T> JavaScript.set(baseList: JsList<T>): JsSet<T> {
    val set = JsSet(baseList = baseList)
    children.add(set)
    return set
}