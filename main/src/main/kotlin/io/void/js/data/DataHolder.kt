package io.void.js.data

import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

class DataHolder<T> internal constructor(private var value: T?, val function: Function, private val js: JavaScript): Keyword {

    override fun render(): String {
        return value as String
    }

    fun set(newValue: T): T? {
        value = newValue
        DataHandler.singleton.update(
            newData = this
        )
        return value
    }

    fun get(): T? {
        return value
    }
}