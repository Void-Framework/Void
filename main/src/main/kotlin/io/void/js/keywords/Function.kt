package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.variable.Variable

open class Function(
    val name: String,
    val arguments: List<String> = emptyList(),
    val body: JavaScript.(Function) -> Unit
): Keyword {

    val children = mutableListOf<Keyword>()
    override var jsReturn: String = ""

    override fun render(): String {
        jsReturn = "function $name(${arguments.joinToString(", ")}) {${children.joinToString(";") { it.render() }}}"
        return jsReturn
    }

    fun run(arguments: List<String>): String {
        return "$name(${arguments.joinToString(", ")})"
    }

    fun put(keyword: Keyword) {
        this.children.add(keyword)
    }

    fun async(): Function {
        jsReturn = "async $jsReturn"
        return this
    }

    fun getArg(name: String): FunctionVariable<*> {
        return FunctionVariable<Any>(name)
    }
}

data class FunctionVariable<T>(override val name: String): Variable<T> {
    override val value: T? = null
    override var jsReturn: String = name
    override fun render(): String {
        return jsReturn
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

data class FunctionRunner(val function: Function, val args: List<String>): Keyword {

    override var jsReturn: String = "${function.name}(${args.joinToString(", ")})"

    override fun render(): String {
        return jsReturn
    }

}

fun JavaScript.run(function: Function, arguments: List<String>): FunctionRunner {
    val run = FunctionRunner(
        function = function,
        args = arguments
    )
    children.add(run)
    return run
}