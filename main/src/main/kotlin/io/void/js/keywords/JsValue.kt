package io.void.js.keywords

import io.void.html.Element
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

interface JsValue<T> {
    fun toJs(): String
}

// Implementation for direct values
data class DirectValue<T>(internal val value: T) : JsValue<T> {
    override fun toJs(): String = when (value) {
        is String -> if (TemplateString.isTemplateString(value)) {
            TemplateString.turnToTemplateString(value)
        } else {
            "\"$value\""
        }
        is Keyword -> value.render()
        is Element -> value.render()
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

data class FunctionValue(internal val function: Function, internal val argsList: JsValue<*> = emptyJsValue()) : JsValue<Any?> {
    override fun toJs(): String = function.run(argsList)
    override fun toString(): String {
        return toJs()
    }
}

// Extension functions to create JsValues
fun <T> T.asJsValue(): JsValue<T> = DirectValue(this)
fun <T> Variable<T>.asJsValue(): JsValue<T> = VariableValue(this)
fun Function.asJsValue(argsList: JsValue<*>): JsValue<Any?> = FunctionValue(this, argsList)
fun emptyJsValue(): JsValue<Nothing> = object : JsValue<Nothing> {
    override fun toJs(): String = ""
}