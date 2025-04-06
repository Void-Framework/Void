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

    inner class Actions {

        fun forEach(): Runnable {
            jsReturn += ".forEach("
            return Runnable()
        }
        fun push(item: String): JsList<T> {
            jsReturn += ".push($item)"
            return this@JsList
        }
        fun pop(): JsList<T> {
            jsReturn += ".pop()"
            return this@JsList
        }
        fun shift(): JsList<T> {
            jsReturn += ".shift()"
            return this@JsList
        }
        fun unshift(item: String): JsList<T> {
            jsReturn += ".unshift($item)"
            return this@JsList
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
            return Runnable()
        }
        fun filter(): Runnable {
            jsReturn += ".filter("
            return Runnable()
        }
        fun includes(item: String): JsList<T> {
            jsReturn += ".includes($item)"
            return this@JsList
        }
    }

    inner class Runnable {
        fun run(function: Function) {
            val random = DataHandler.randomString(5)
            jsReturn += "$random => ${function.run(listOf(random))})"
        }
    }
}

inline fun <reified T> JavaScript.jsList(arguments: List<T>): JsList<T> {
    val list = JsList(arguments = arguments)
    children.add(list)
    return list.initialize() as JsList<T>
}