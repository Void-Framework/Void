package io.void.js.keywords

import io.void.html.Element
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable
import io.void.js.Function

interface JsValue<T> {
    fun toJs(): String
}

// Implementation for direct values
data class DirectValue<T>(internal val value: T) : JsValue<T> {
    override fun toJs(): String = when (value) {
        is String -> if (TemplateString.isTemplateString(value)) {
            TemplateString.turnToTemplateString(value)
        } else {
            if (value.contains("\"")) {
                "'$value'"
            } else {
                "\"$value\""
            }
        }
        is Keyword -> {
            val render = value.render()
            if (render.contains("\"")) {
                "'$render'"
            } else {
                "\"$render\""
            }
        }
        is Element -> {
            val render = value.render()
            if (render.contains("\"")) {
                "'$render'"
            } else {
                "\"$render\""
            }
        }
        is Iterable<*> -> value.joinToString(",") { DirectValue(it).toJs() }
        is Number, Boolean -> value.toString()
        else -> "\"${value.toString()}\""
    }
    override fun toString(): String {
        return toJs()
    }
}

// Implementation for variables
data class VariableValue<T>(internal val variable: Variable<T>) : JsValue<T> {
    override fun toJs(): String = variable.name
    override fun toString(): String {
        return toJs()
    }
}

data class FunctionValue<T>(internal val function: Function<T>, internal val argsList: JsValue<*> = emptyJsValue()) : JsValue<T> {
    override fun toJs(): String = function.run(argsList)
    override fun toString(): String {
        return toJs()
    }
}

// Extension functions to create JsValues
fun <T> T.asJsValue(): JsValue<T> = DirectValue(this)
fun <T> Variable<T>.asJsValue(): JsValue<T> = VariableValue(this)
fun <T> Function<T>.asJsValue(argsList: JsValue<*>): JsValue<T> = FunctionValue(this, argsList)
fun emptyJsValue(): JsValue<Nothing> = object : JsValue<Nothing> {
    override fun toJs(): String = ""
}