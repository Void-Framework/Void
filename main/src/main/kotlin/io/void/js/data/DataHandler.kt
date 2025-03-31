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
         val funcName = randomString(5)
         // Create the function first
         val function = js.function(funcName, listOf("newValue")) {
             val Return = Return(_value = "newValue")
             it.children.add(Return)
         }

         return DataHolder(
             value = value,
             function = function,
             js = js
         )
    }
}

fun <T> JavaScript.setData(value: T): DataHolder<T> {
    val dataHolder = DataHandler.singleton.create(value = value, js = this)
    children.add(dataHolder)
    return dataHolder
}