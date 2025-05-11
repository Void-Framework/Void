package io.void.js

import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.datastructures.JsDatastructure
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.variable.Variable

sealed class JavaScript(open val runBeforeLoad: Boolean = false) {

    constructor(runBeforeLoad: Boolean = false, code: JavaScript.() -> Unit): this(runBeforeLoad) {
        this.apply(code)
    }

    open val children = mutableListOf<Keyword>()

    open fun render(): String {
        if (children.isEmpty()) {
            return ""
        }

        val js = children.mapIndexed { index, keyword ->
            val rendered = if (keyword.await) keyword.awaitRender() else keyword.render()
            if (!rendered.endsWith(";")) {
                // Check if next line starts with a dot or is a function/class declaration
                val nextLine = getNextLine(index, children)

                renderKeyword(rendered, nextLine)
            } else rendered
        }.joinToString("\n")
        return js
    }
    internal fun getNextLine(index: Int, list: MutableList<Keyword>): String? {
        return if (index < children.size - 1) {
            children[index + 1].render()
        } else null
    }
    internal fun renderKeyword(rendered: String, nextLine: String?): String {
        return if (nextLine?.startsWith(".") == true ||
            rendered.contains("function") ||
            (rendered.contains("class")) && !rendered.contains("class=")) {
            rendered
        } else {
            "$rendered;"
        }
    }
}

class Js(override val runBeforeLoad: Boolean = false, val code: JavaScript.() -> Unit): JavaScript(runBeforeLoad, code)

open class Function<T>(
    val name: String,
    val arguments: List<String> = emptyList(),
    val body: JavaScript.(List<FunctionVariable<*>>) -> Unit
): JavaScript(), Keyword {

    override val children = mutableListOf<Keyword>()
    private var async = false
    override var jsReturn: String = ""
    protected val functionArgs = arguments.map { FunctionVariable<Any?>(it) }

    init {
        this.body(functionArgs)
    }

    override fun render(): String {
        val js = children.mapIndexed { index, keyword ->
            val rendered = if (keyword.await) keyword.awaitRender() else keyword.render()
            if (!rendered.endsWith(";")) {
                // Check if next line starts with a dot or is a function/class declaration
                val nextLine = getNextLine(index, children)

                renderKeyword(rendered, nextLine)
            } else rendered
        }.joinToString("\n")
        jsReturn = "${if (async) "async " else ""}function $name(${arguments.joinToString(", ")}) {$js}"
        return jsReturn
    }

    open fun run(arguments: JsValue<*>): String {
        return if (arguments != emptyJsValue()) "$name($arguments)" else "$name()"
    }

    fun async(): Function<T> {
        async = true
        return this
    }
}

data class FunctionVariable<T>(override val name: String): Variable<T> {
    override val value: T? = null
    override var jsReturn: String = name
    override fun render(): String {
        return jsReturn
    }
}

fun <T> JavaScript.function(name: String, arguments: List<String>, body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Function<T> {
    val function = Function<T>(
        name = name,
        arguments = arguments,
        body = body
    )
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