package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable
import java.net.URL

class Window(window: JsValue<*>? = null): Keyword {

    override var jsReturn: String = window?.toJs() ?: "window"

    override fun render(): String {
        return jsReturn
    }

    fun alert(message: JsValue<*>?): Void {
        jsReturn += ".alert(${message ?: "\"\""})"
        return Void()
    }
    fun open(url: JsValue<*>? = null, name: JsValue<*>? = null, specs: JsValue<*>? = null): Window {
        jsReturn += ".open(${url ?: "\"\""}, ${name ?: "\"\""}, ${specs ?: "\"\""})"
        return Window()
    }
    fun confirm(message: JsValue<*>): Void {
        jsReturn += ".confirm($message)"
        return Void()
    }
    fun history(): History {
        jsReturn += ".history"
        return History()
    }
    fun href(url: JsValue<*>?): Void {
        jsReturn += ".location.href = ${url ?: "\"\""}"
        return Void()
    }
    fun prompt(message: JsValue<*>? = null, defaultText: JsValue<*>? = null): Void {
        jsReturn += ".prompt(${message ?: "\"\""}, ${defaultText ?: "\"\""})"
        return Void()
    }
    fun scrollBy(x: JsValue<*>, y: JsValue<*>): Void {
        jsReturn += ".scrollBy($x, $y)"
        return Void()
    }
    fun scrollTo(x: JsValue<*>, y: JsValue<*>): Void {
        jsReturn += ".scrollTo($x, $y)"
        return Void()
    }

    inner class History {
        fun back(): Void {
            jsReturn += ".back()"
            return Void()
        }
        fun forward(): Void {
            jsReturn += ".forward()"
            return Void()
        }
        fun go(number: JsValue<*>): Void {
            jsReturn += ".go($number)"
            return Void()
        }
    }
}

fun JavaScript.window(): Window {
    val window = Window()
    children.add(window)
    return window
}