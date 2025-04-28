package io.void.js.keywords.datastructures

import io.void.js.data.DataHandler
import io.void.js.Function
import io.void.js.data.randomString
import io.void.js.keywords.Keyword
import io.void.js.keywords.asJsValue

interface JsDatastructure: Keyword {

    fun initialize(): JsDatastructure

    fun forEach(): Runnable {
        jsReturn += ".forEach("
        return Runnable(this)
    }
}

class Runnable(var keyword: Keyword) {
    fun <T> run(function: Function<T>): Runnable {
        val random = String.randomString(5)
        keyword.jsReturn += "$random => ${function.run(function.getArg(random).asJsValue())})"
        return this
    }
}