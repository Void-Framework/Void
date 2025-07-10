package io.void.js.type

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword

class Parse: Keyword {

    override var jsReturn: String = ""
    override fun render(): String {
        return jsReturn
    }

    fun parseInt(string: JsValue<String>, radix: JsValue<Int>? = null): Int {
        jsReturn = "parseInt($string${if (radix == null) "" else ", $radix"})"
        return 0
    }
    fun parseFloat(string: JsValue<String>): Int {
        jsReturn = "parseFloat($string)"
        return 0
    }
    fun isNaN(value: JsValue<*>): Boolean {
        jsReturn = "isNaN($value)"
        return true
    }
    fun isFinite(value: JsValue<*>): Boolean {
        jsReturn = "isFinite($value)"
        return true
    }
}

fun JavaScript.parseInt(string: JsValue<String>, radix: JsValue<Int>? = null): Int {
    val parse = Parse()
    children.add(parse)
    return parse.parseInt(string, radix)
}
fun JavaScript.parseFloat(string: JsValue<String>): Int {
    val parse = Parse()
    children.add(parse)
    return parse.parseFloat(string)
}
fun JavaScript.isNaN(value: JsValue<*>): Boolean {
    val parse = Parse()
    children.add(parse)
    return parse.isNaN(value)
}
fun JavaScript.isFinite(value: JsValue<*>): Boolean {
    val parse = Parse()
    children.add(parse)
    return parse.isFinite(value)
}