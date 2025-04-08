package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import kotlin.collections.List

data class JsList<T>(val arguments: List<T>): JsDatastructure {

    override var jsReturn = ""
    private var inside = StringBuilder("")

    init {
        arguments.forEach {
            inside.append("$it,")
        }
        if (arguments.isNotEmpty()) {
            inside.setLength(inside.length - 1)
        }
    }

    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "[$inside]"
        return this
    }
    fun push(item: String): Void {
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
    fun unshift(item: String): Void {
        jsReturn += ".unshift($item)"
        return Void()
    }
    fun splice(index: Int, count: Int? = null, item: List<String>?): JsList<T> {
        jsReturn += ".splice($index${if (count != null) {
            ", $count"
        } else {
            ""
        }
        }${if (item != null) {
            ", ${item.joinToString(", ")}"
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
    fun includes(item: String): Void {
        jsReturn += ".includes($item)"
        return Void()
    }
}

inline fun <reified T> JavaScript.jsList(arguments: List<T>): JsList<T> {
    val list = JsList(arguments = arguments)
    children.add(list)
    return list.initialize() as JsList<T>
}