package io.void.js.data

import io.void.js.EventDispatcher
import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Return
import io.void.js.keywords.function
import java.util.UUID

class DataHandler private constructor() {

    companion object {
        val singleton = DataHandler()

        fun randomString(length: Int): String {
            val chars = ('a'..'z') + ('A'..'Z')
            return (1..length)
                .map { chars.random() }
                .joinToString("")
        }
    }

     internal fun <T> create(value: T, js: JavaScript): DataHolder<T> {
        return DataHolder(
            value = value,
            function = js.function(randomString(5), listOf("newValue")) {
                Return(_value = "newValue")
            },
            js = js
        )
    }
}

fun <T> JavaScript.setData(value: T): DataHolder<T> {
    val dataHolder = DataHandler.singleton.create(value = value, js = this)
    children.add(dataHolder)
    return dataHolder
}