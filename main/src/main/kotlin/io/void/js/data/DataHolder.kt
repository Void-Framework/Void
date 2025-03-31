package io.void.js.data

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.function

class DataHolder<T> internal constructor(private var value: T?, val function: Function, private val js: JavaScript): Keyword {

    override fun render(): String {
        return "let ${DataHandler.randomString(6)} = $value;${function.render()}"
    }

    fun set(newValue: T): T? {
        js.children.add(FunctionRunner(
            function = function,
            args = listOf(newValue as String)
        ))
        return value
    }

    fun get(): T? {
        return value
    }

    internal inner class FunctionRunner(val function: Function, val args: List<String>): Keyword {

        override fun render(): String {
            return function.run(args)
        }

    }
}