package io.void.js.data

import io.void.js.EventDispatcher
import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Return
import io.void.js.keywords.function
import java.util.UUID

class DataHandler private constructor() : IDataHandler {

    companion object {
        val singleton = DataHandler()
    }

    override fun <T> create(value: T, js: JavaScript): DataHolder<T> {
        return DataHolder(
            value = value,
            function = js.function(randomString(5), listOf("newValue")) {
                Return(_value = "newValue")
            },
            js = js
        )
    }

    override fun <T> update(newData: DataHolder<T>) {
        EventDispatcher.callEvent(DataHolder::class.java, newData)
    }

    private fun randomString(length: Int): String {
        val chars = ('a'..'z') + ('A'..'Z')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}