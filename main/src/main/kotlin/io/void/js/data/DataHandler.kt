package io.void.js.data

import io.void.js.JavaScript
import io.void.js.Function
import io.void.js.function
import io.void.js.keywords.Return
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

     inline fun <reified T> create(value: T, js: JavaScript, uuid: UUID): DataHolder<T> {
         val funcName = randomString(5)
         // Create the function first
         val function = js.function<T>(funcName, listOf("newValue")) {
             Return("newValue")
         }

         return DataHolder(
             value = value,
             function = function,
             js = js,
             uuid = uuid
         )
    }
}

inline fun <reified T> JavaScript.setData(value: T): DataHolder<T> {
    val uuid = UUID.randomUUID()
    val dataHolder = DataHandler.singleton.create(value = value, js = this, uuid = uuid)
    children.add(dataHolder)
    return dataHolder
}