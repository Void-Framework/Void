package io.void.js.keywords

import io.void.js.JavaScript

data class Function(
    val name: String,
    val arguments: List<String> = emptyList(),
    val body: JavaScript.() -> Unit
): Keyword {

    override fun render(): String {
        return "function $name(${arguments.joinToString(", ")}) {${JavaScript(body).render()}}"
    }

    fun run(arguments: List<String>): String {
        return "$name(${arguments.joinToString(", ")});"
    }
}

fun JavaScript.function(name: String, arguments: List<String>, body: JavaScript.() -> Unit): Function {
    val function = Function(
        name = name,
        arguments = arguments,
        body = body
    )
    children.add(function)
    return function
}