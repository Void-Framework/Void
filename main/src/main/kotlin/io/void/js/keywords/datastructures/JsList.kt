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
        fun push(item: String) {
            jsReturn += ".push($item)"
        }
        fun pop() {
            jsReturn += ".pop()"
        }
        fun shift() {
            jsReturn += ".shift()"
        }
        fun unshift(item: String) {
            jsReturn += ".unshift($item)"
        }
        fun splice(index: Int, count: Int? = null, item: List<String>?) {
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
        }
        fun map(): Runnable {
            jsReturn += ".map("
            return Runnable()
        }
        fun filter(): Runnable {
            jsReturn += ".filter("
            return Runnable()
        }
        fun includes(item: String) {
            jsReturn += ".includes($item)"
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