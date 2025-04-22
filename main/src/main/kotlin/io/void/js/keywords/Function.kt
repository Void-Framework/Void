package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.variable.Variable

open class Function<T>(
    val name: String,
    val arguments: List<String> = emptyList(),
    val body: JavaScript.(Function<T>) -> Unit
): Keyword {

    val children = mutableListOf<Keyword>()
    private var async = false
    override var jsReturn: String = ""

    override fun render(): String {
        jsReturn = "${if (async) "async " else ""}function $name(${arguments.joinToString(", ")}) {${children.joinToString(";") { it.render() }}}"
        return jsReturn
    }

    open fun run(arguments: JsValue<*>): String {
        return "$name($arguments)"
    }

    fun put(keyword: Keyword) {
        this.children.add(keyword)
    }

    fun async(): Function<T> {
        async = true
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

fun <T> JavaScript.function(name: String, arguments: List<String>, body: JavaScript.(Function<T>) -> Unit): Function<T> {
    val function = Function(
        name = name,
        arguments = arguments,
        body = body
    )
    body(function)
    children.add(function)
    return function
}

data class FunctionRunner<T>(val function: Function<T>, val args: JsValue<*>): Keyword {

    override var jsReturn: String = "${function.name}($args)"

    override fun render(): String {
        return jsReturn
    }

}

fun <T> JavaScript.run(function: Function<T>, arguments: JsValue<*>): FunctionRunner<T> {
    val run = FunctionRunner(
        function = function,
        args = arguments
    )
    children.add(run)
    return run
}