package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import kotlin.collections.List

data class JsList<T>(val arguments: List<T>): Keyword {

    override var jsReturn = ""

    override fun render(): String {
        return "$jsReturn;"
    }

    fun initialize(): JsList<T> {
        jsReturn = "[${arguments.joinToString(", ")}]"
        return JsList(arguments)
    }

    inner class Actions {

        fun forEach(function: Function) {
            val random = DataHandler.randomString(5)
            jsReturn += ".forEach($random => ${function.run(listOf(random))})"
        }
    }
}

inline fun <reified T> JavaScript.forEach(list: JsList<T>, function: Function) {
    list.Actions().forEach(function)
    children.add(list)
}