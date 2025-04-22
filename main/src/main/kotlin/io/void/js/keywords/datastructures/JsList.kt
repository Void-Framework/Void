package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.keywords.Function
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import kotlin.collections.List

data class JsList<T>(val arguments: JsValue<T>): JsDatastructure {

    override var jsReturn = ""
    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "[$arguments]"
        return this
    }
    fun push(item: JsValue<T>): JsList<T> {
        jsReturn += ".push($item)"
        return this
    }
    fun pop(): JsList<T> {
        jsReturn += ".pop()"
        return this
    }
    fun shift(): JsList<T> {
        jsReturn += ".shift()"
        return this
    }
    fun unshift(item: JsValue<T>): JsList<T> {
        jsReturn += ".unshift($item)"
        return this
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
        return this@JsList
    }
    fun map(): Runnable {
        jsReturn += ".map("
        return Runnable(this)
    }
    fun filter(): Runnable {
        jsReturn += ".filter("
        return Runnable(this)
    }
    fun includes(item: JsValue<*>): JsList<T> {
        jsReturn += ".includes($item)"
        return this
    }
}

inline fun <reified T> JavaScript.jsList(arguments: JsValue<*>): JsList<T> {
    val list = JsList(arguments = arguments)
    children.add(list)
    @Suppress("UNCHECKED_CAST")
    return list.initialize() as JsList<T>
}