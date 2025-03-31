package io.void.js.data

import io.void.js.EventDispatcher
import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.function

class DataHolder<T> internal constructor(private var value: T?, val function: Function, private val js: JavaScript): Keyword {

    override var jsReturn: String = ""

    override fun render(): String {
        val value = when (value) {
            is String -> value?.toString()?.let { "\"$it\"" } ?: "null"
            else -> value?.toString() ?: "null"
        }
        // Remove the semicolon here since JavaScript.render() will handle it
        return "let ${DataHandler.randomString(6)} = $value;"
    }

    fun set(newValue: T): T? {
        value = newValue
        js.children.add(FunctionRunner(
            function = function,
            args = listOf(newValue as String)
        ))
        EventDispatcher.callEvent(DataHolder::class.java, this)
        return value
    }

    fun get(): T? {
        return value
    }

    internal inner class FunctionRunner(val function: Function, val args: List<String>): Keyword {

        override var jsReturn: String = ""

        override fun render(): String {
            return function.run(args)
        }

    }
}