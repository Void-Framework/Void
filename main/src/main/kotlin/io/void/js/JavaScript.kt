package io.void.js

import io.void.js.keywords.Const
import io.void.js.keywords.Keyword
import io.void.js.keywords.Let

class JavaScript(val runBeforeLoad: Boolean = false, val code: JavaScript.() -> Unit) {

    val children = mutableListOf<Keyword>()

    init {
        this.apply(code)
    }

    fun render(): String {
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

    fun <T> declare(constant: Boolean = false, name: String, value: T): Variable {
        val variable = if (constant) {
            Variable.Constant(
                Const(
                    name = name,
                    value = value
                )
            )
        } else {
            Variable.NonConstant(
                Let(
                    name = name,
                    value = value
                )
            )
        }
        when (variable) {
            is Variable.Constant -> children.add(variable.variable)
            is Variable.NonConstant -> children.add(variable.variable)
        }
        return variable
    }

    sealed class Variable {
        class Constant(val variable: Const<*>): Variable()
        class NonConstant(val variable: Let<*>): Variable()
    }
}