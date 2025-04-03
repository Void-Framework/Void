package io.void.js.keywords

import io.void.js.JavaScript

open class Function(
    val name: String,
    val arguments: List<String> = emptyList(),
    val body: JavaScript.(Function) -> Unit
): Keyword {

    val children = mutableListOf<Keyword>()
    override var jsReturn: String = ""

    override fun render(): String {
        return "function $name(${arguments.joinToString(", ")}) {${children.joinToString(";") { it.render() }}}"
    }

    fun run(arguments: List<String>): String {
        return "$name(${arguments.joinToString(", ")})"
    }

    fun put(keyword: Keyword) {
        this.children.add(keyword)
    }
}

fun JavaScript.function(name: String, arguments: List<String>, body: JavaScript.(Function) -> Unit): Function {
    val function = Function(
        name = name,
        arguments = arguments,
        body = body
    )
    body(function)
    children.add(function)
    return function
}