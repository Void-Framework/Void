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
    fun push(item: JsValue<T>): Void {
        jsReturn += ".push($item)"
        return Void()
    }
    fun pop(): Void {
        jsReturn += ".pop()"
        return Void()
    }
    fun shift(): Void {
        jsReturn += ".shift()"
        return Void()
    }
    fun unshift(item: JsValue<T>): Void {
        jsReturn += ".unshift($item)"
        return Void()
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
    fun includes(item: JsValue<*>): Void {
        jsReturn += ".includes($item)"
        return Void()
    }
}

inline fun <reified T> JavaScript.jsList(arguments: JsValue<*>): JsList<T> {
    val list = JsList(arguments = arguments)
    children.add(list)
    @Suppress("UNCHECKED_CAST")
    return list.initialize() as JsList<T>
}