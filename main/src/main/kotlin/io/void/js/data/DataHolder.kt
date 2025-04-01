package io.void.js.data

import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.js.EventDispatcher
import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.function
import java.util.UUID

class DataHolder<T> internal constructor(private var value: T?, val function: Function, private val js: JavaScript, val uuid: UUID): Keyword {

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

    internal fun get(): T? {
        return value
    }

    internal inner class FunctionRunner(val function: Function, val args: List<String>): Keyword {

        override var jsReturn: String = ""

        override fun render(): String {
            return function.run(args)
        }

    }
}

fun Element.get(dataHolder: DataHolder<*>): Element {
    val fractal = Fractal(_text = dataHolder.get().toString())
    this.parent.attributes[AttributeNames.V_DATAHOLD] = dataHolder.uuid.toString()
    return fractal
}