package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.Function
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.asJsValue
import io.void.js.keywords.emptyJsValue
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
    fun emptyList(): JsList<T> {
        jsReturn = "[]"
        return this
    }

    fun push(item: JsValue<T>): JsValue<Int> {
        jsReturn += ".push($item)"
        return 0.asJsValue()
    }
    fun pop(): JsValue<T?> {
        jsReturn += ".pop()"
        return null.asJsValue()
    }
    fun shift(): JsValue<T?> {
        jsReturn += ".shift()"
        return null.asJsValue()
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
    fun includes(item: JsValue<*>): JsValue<Boolean> {
        jsReturn += ".includes($item)"
        return true.asJsValue()
    }
}

inline fun <reified T> JavaScript.jsList(arguments: JsValue<*>): JsList<T> {
    val list = JsList(arguments = arguments)
    children.add(list)
    @Suppress("UNCHECKED_CAST")
    return list.initialize() as JsList<T>
}
inline fun <reified T> JavaScript.emptyJsList(): JsList<T> {
    val list = JsList(arguments = emptyJsValue())
    children.add(list)
    @Suppress("UNCHECKED_CAST")
    return list.emptyList() as JsList<T>
}