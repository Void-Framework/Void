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

    fun alert(message: JsValue<*>?): Window {
        jsReturn += ".alert(${message ?: "\"\""})"
        return this
    }
    fun open(url: JsValue<*>? = null, name: JsValue<*>? = null, specs: JsValue<*>? = null): Window {
        jsReturn += ".open(${url ?: "\"\""}, ${name ?: "\"\""}, ${specs ?: "\"\""})"
        return Window()
    }
    fun confirm(message: JsValue<*>): Window {
        jsReturn += ".confirm($message)"
        return this
    }
    fun history(): History {
        jsReturn += ".history"
        return History()
    }
    fun href(url: JsValue<*>?): Window {
        jsReturn += ".location.href = ${url ?: "\"\""}"
        return this
    }
    fun prompt(message: JsValue<*>? = null, defaultText: JsValue<*>? = null): Window {
        jsReturn += ".prompt(${message ?: "\"\""}, ${defaultText ?: "\"\""})"
        return this
    }
    fun scrollBy(x: JsValue<*>, y: JsValue<*>): Window {
        jsReturn += ".scrollBy($x, $y)"
        return this
    }
    fun scrollTo(x: JsValue<*>, y: JsValue<*>): Window {
        jsReturn += ".scrollTo($x, $y)"
        return this
    }

    inner class History {
        fun back(): History {
            jsReturn += ".back()"
            return this
        }
        fun forward(): History {
            jsReturn += ".forward()"
            return this
        }
        fun go(number: JsValue<*>): History {
            jsReturn += ".go($number)"
            return this
        }
    }
}

fun JavaScript.window(): Window {
    val window = Window()
    children.add(window)
    return window
}