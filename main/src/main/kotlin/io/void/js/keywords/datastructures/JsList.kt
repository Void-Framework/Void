package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import kotlin.collections.List

data class JsList<T>(val arguments: List<T>): Keyword {

    override var jsReturn = ""

    override fun render(): String {
        return jsReturn
    }

    fun initialize(): JsList<T> {
        jsReturn = "[${arguments.joinToString(", ")}]"
        return JsList(arguments)
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

inline fun <reified T> JavaScript.forEach(list: JsList<T>): JsList<T>.Runnable {
    val runnable = list.Actions().forEach()
    children.add(list)
    return runnable
}
inline fun <reified T> JavaScript.map(list: JsList<T>): JsList<T>.Runnable {
    val runnable = list.Actions().map()
    children.add(list)
    return runnable
}
inline fun <reified T> JavaScript.filter(list: JsList<T>): JsList<T>.Runnable {
    val runnable = list.Actions().filter()
    children.add(list)
    return runnable
}
inline fun <reified T> JavaScript.jsList(arguments: List<T>): JsList<T> {
    val list = JsList(arguments = arguments).initialize()
    children.add(list)
    return list
}