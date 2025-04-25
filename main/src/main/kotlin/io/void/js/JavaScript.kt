package io.void.js

import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.datastructures.JsDatastructure
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.variable.Variable

sealed class JavaScript(val runBeforeLoad: Boolean = false) {

    constructor(runBeforeLoad: Boolean = false, code: JavaScript.() -> Unit) : this(runBeforeLoad) {
        this.apply(code)
    }

    open val children = mutableListOf<Keyword>()

    open fun render(): String {
        if (children.isEmpty()) {
            return ""
        }

        val js = children.mapIndexed { index, keyword ->
            val rendered = keyword.render()
            if (!rendered.endsWith(";")) {
                // Check if next line starts with a dot or is a function/class declaration
                val nextLine = if (index < children.size - 1) {
                    children[index + 1].render()
                } else null

                if (nextLine?.startsWith(".") == true ||
                    rendered.contains("function") ||
                    rendered.contains("class")) {
                    rendered
                } else {
                    "$rendered;"
                }
            } else rendered
        }.joinToString("\n")
        return js
    }
}

open class Function<T>(
    val name: String,
    val arguments: List<String> = emptyList(),
    val body: JavaScript.() -> Unit
): JavaScript(code = body), Keyword {

    override val children = mutableListOf<Keyword>()
    private var async = false
    override var jsReturn: String = ""

    override fun render(): String {
        jsReturn = "${if (async) "async " else ""}function $name(${arguments.joinToString(", ")}) {${children.joinToString(";") { it.render() }}}"
        return jsReturn
    }

    open fun run(arguments: JsValue<*>): String {
        return if (arguments != emptyJsValue()) "$name($arguments)" else "$name()"
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

fun <T> JavaScript.function(name: String, arguments: List<String>, body: JavaScript.() -> Unit): Function<T> {
    val function = Function<T>(
        name = name,
        arguments = arguments,
        body = body
    )
    body(function)
    children.add(function)
    return function
}

data class FunctionRunner<T>(val function: Function<T>, val args: JsValue<*>): Keyword {

    override var jsReturn: String = "${function.name}(${args.toJs()})"

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