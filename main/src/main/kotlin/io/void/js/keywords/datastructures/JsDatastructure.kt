package io.void.js.keywords.datastructures

import io.void.js.data.DataHandler
import io.void.js.keywords.Function
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
    fun run(function: Function): Runnable {
        val random = DataHandler.randomString(5)
        keyword.jsReturn += "$random => ${function.run(function.getArg(random).asJsValue())})"
        return this
    }
}