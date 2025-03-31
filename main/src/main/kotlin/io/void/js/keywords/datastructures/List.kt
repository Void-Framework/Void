package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Keyword
import kotlin.collections.List

data class List<T>(val arguments: List<T>): Keyword {

    private var jsReturn = ""

    override fun render(): String {
        return "$jsReturn;"
    }

    fun forEach(vName: T, body: JavaScript.(value: T) -> Unit) {
        jsReturn += ".forEach($vName => {${JavaScript { body(vName) }.render()}})"
    }

    fun initialize(): io.void.js.keywords.datastructures.List<T> {
        jsReturn = "[${arguments.joinToString(", ")}]"
        return List(arguments)
    }
}