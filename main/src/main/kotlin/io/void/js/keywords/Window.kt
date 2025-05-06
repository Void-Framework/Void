package io.void.js.keywords

import io.void.js.Function
import io.void.js.JavaScript
import io.void.js.keywords.storage.Local
import io.void.js.keywords.storage.Session
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
    fun session(): Session {
        jsReturn += ".sessionStorage"
        return Session()
    }
    fun local(): Local {
        jsReturn += ".localStorage"
        return Local()
    }
    fun timeout(function: JsValue<Function<Nothing>>, delay: Int = 0, args: JsValue<*>? = null): JsValue<Int> {
        jsReturn += ".setTimeout($function${if (delay != 0) ", $delay" else ""}${if (args != null) ", $args" else ""})"
        return 0.asJsValue()
    }
    fun timeout(id: JsValue<Int>): Reference<Window> {
        jsReturn += ".clearTimeout($id)"
        return this.refer()
    }
    fun interval(function: JsValue<Function<Nothing>>, delay: Int = 0, args: JsValue<*>? = null): JsValue<Int> {
        jsReturn += ".setInterval($function${if (delay != 0) ", $delay" else ""}${if (args != null) ", $args" else ""})"
        return 0.asJsValue()
    }
    fun interval(id: JsValue<Int>): Reference<Window> {
        jsReturn += ".clearInterval($id)"
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

fun JavaScript.alert(message: JsValue<String>, window: JsValue<*>? = null) {
    val window = Window(window)
    children.add(window)
    window.alert(message)
}
fun JavaScript.open(url: JsValue<*>? = null, name: JsValue<String>? = null, specs: JsValue<*>? = null, window: JsValue<*>? = null): Window {
    val window = Window(window)
    children.add(window)
    val newWindow = window.open(url, name, specs)
    children.add(newWindow)
    return newWindow
}
fun JavaScript.confirm(message: JsValue<String>, window: JsValue<*>? = null): JsValue<Boolean> {
    val window = Window(window)
    children.add(window)
    return window.confirm(message)
}
fun JavaScript.history(window: JsValue<*>? = null): History {
    val window = Window(window)
    children.add(window)
    val history = window.history()
    children.add(history)
    return history
}
fun JavaScript.href(url: JsValue<*>?, window: JsValue<*>? = null) {
    val window = Window(window)
    children.add(window)
    window.href(url)
}
fun JavaScript.prompt(message: JsValue<String>? = null, defaultText: JsValue<String>? = null, window: JsValue<*>? = null): JsValue<String> {
    val window = Window(window)
    children.add(window)
    return window.prompt(message, defaultText)
}
fun JavaScript.scrollBy(x: JsValue<Int>, y: JsValue<Int>, window: JsValue<*>? = null) {
    val window = Window(window)
    children.add(window)
    window.scrollBy(x, y)
}
fun JavaScript.scrollTo(x: JsValue<Int>, y: JsValue<Int>, window: JsValue<*>? = null) {
    val window = Window(window)
    children.add(window)
    window.scrollTo(x, y)
}
fun JavaScript.session(window: JsValue<*>? = null): Session {
    val window = Window(window)
    children.add(window)
    val session = window.session()
    children.add(session)
    return session
}
fun JavaScript.local(window: JsValue<*>? = null): Local {
    val window = Window(window)
    children.add(window)
    val local = window.local()
    children.add(local)
    return local
}
fun JavaScript.timeout(function: JsValue<Function<Nothing>>, delay: Int = 0, args: JsValue<*>? = null, window: JsValue<*>? = null): JsValue<Int> {
    val window = Window(window)
    children.add(window)
    return window.timeout(function, delay, args)
}
fun JavaScript.timeout(id: JsValue<Int>, window: JsValue<*>? = null) {
    val window = Window(window)
    children.add(window)
    window.timeout(id)
}
fun JavaScript.interval(function: JsValue<Function<Nothing>>, delay: Int = 0, args: JsValue<*>? = null, window: JsValue<*>? = null): JsValue<Int> {
    val window = Window(window)
    children.add(window)
    return window.interval(function, delay, args)
}
fun JavaScript.interval(id: JsValue<Int>, window: JsValue<*>? = null) {
    val window = Window(window)
    children.add(window)
    window.interval(id)
}