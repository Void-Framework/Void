package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable
import java.net.URL

class Window(window: JsValue<*>? = null): Keyword {

    override var jsReturn: String = window?.toJs() ?: "window"

    override fun render(): String {
        return jsReturn
    }

    fun alert(message: JsValue<String>?): Reference<Window> {
        jsReturn += ".alert(${message ?: "\"\""})"
        return this.refer()
    }
    fun open(url: JsValue<*>? = null, name: JsValue<String>? = null, specs: JsValue<*>? = null): Window {
        jsReturn += ".open(${url ?: "\"\""}, ${name ?: "\"\""}, ${specs ?: "\"\""})"
        return Window()
    }
    fun confirm(message: JsValue<String>): JsValue<Boolean> {
        jsReturn += ".confirm($message)"
        return true.asJsValue()
    }
    fun history(): History {
        jsReturn += ".history"
        return History()
    }
    fun href(url: JsValue<*>?): Reference<Window> {
        jsReturn += ".location.href = ${url ?: "\"\""}"
        return this.refer()
    }
    fun prompt(message: JsValue<String>? = null, defaultText: JsValue<String>? = null): JsValue<String> {
        jsReturn += ".prompt(${message ?: "\"\""}, ${defaultText ?: "\"\""})"
        return "".asJsValue()
    }
    fun scrollBy(x: JsValue<Int>, y: JsValue<Int>): Reference<Window> {
        jsReturn += ".scrollBy($x, $y)"
        return this.refer()
    }
    fun scrollTo(x: JsValue<Int>, y: JsValue<Int>): Reference<Window> {
        jsReturn += ".scrollTo($x, $y)"
        return this.refer()
    }
}

class History: Keyword {
    override var jsReturn: String = ""
    override fun render(): String {
        return jsReturn
    }

    fun back(): Reference<History> {
        jsReturn += ".back()"
        return this.refer()
    }
    fun forward(): Reference<History> {
        jsReturn += ".forward()"
        return this.refer()
    }
    fun go(number: JsValue<Int>): Reference<History> {
        jsReturn += ".go($number)"
        return this.refer()
    }
}

fun JavaScript.window(): Window {
    val window = Window()
    children.add(window)
    return window
}