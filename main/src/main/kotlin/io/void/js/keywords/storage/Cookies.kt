package io.void.js.keywords.storage

import io.void.generated.Object
import io.void.js.JavaScript
import io.void.js.keywords.Call
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.async.Promise
import io.void.js.keywords.datastructures.JsObject
import io.void.js.keywords.refer

class Cookies: Keyword {

    override var jsReturn: String = "cookieStore"
    override fun render(): String {
        return jsReturn
    }

    fun delete(name: JsValue<*>, call: (Promise) -> Unit): Reference<Cookies> {
        jsReturn += ".delete($name)"
        return applyMethods(call, Promise(), this)
    }
    fun get(name: JsValue<*>, call: (Promise) -> Unit): Reference<Cookies> {
        jsReturn += ".get($name)"
        return applyMethods(call, Promise(), this)
    }
    fun getAll(name: JsValue<*>, call: (Promise) -> Unit): Reference<Cookies> {
        jsReturn += ".getAll($name)"
        return applyMethods(call, Promise(), this)
    }
    fun getAll(call: (Promise) -> Unit): Reference<Cookies> {
        jsReturn += ".getAll()"
        return applyMethods(call, Promise(), this)
    }
    fun set(name: JsValue<String>, value: JsValue<String>, call: (Promise) -> Unit): Reference<Cookies> {
        jsReturn += ".set($name, $value)"
        return applyMethods(call, Promise(), this)
    }
    fun set(options: JsValue<Object>, call: (Promise) -> Unit): Reference<Cookies> {
        jsReturn += ".set($options)"
        return applyMethods(call, Promise(), this)
    }
}

fun JavaScript.delete(name: JsValue<*>, call: (Promise) -> Unit): Reference<Cookies> {
    val cookies = Cookies()
    children.add(cookies)
    cookies.delete(name, call)
    return cookies.refer()
}
fun JavaScript.get(name: JsValue<*>, call: (Promise) -> Unit): Reference<Cookies> {
    val cookies = Cookies()
    children.add(cookies)
    cookies.get(name, call)
    return cookies.refer()
}
fun JavaScript.getAll(name: JsValue<*>, call: (Promise) -> Unit): Reference<Cookies> {
    val cookies = Cookies()
    children.add(cookies)
    cookies.getAll(name, call)
    return cookies.refer()
}
fun JavaScript.getAll(call: (Promise) -> Unit): Reference<Cookies> {
    val cookies = Cookies()
    children.add(cookies)
    cookies.getAll(call)
    return cookies.refer()
}
fun JavaScript.set(name: JsValue<String>, value: JsValue<String>, call: (Promise) -> Unit): Reference<Cookies> {
    val cookies = Cookies()
    children.add(cookies)
    cookies.set(name, value, call)
    return cookies.refer()
}
fun JavaScript.set(options: JsValue<Object>, call: (Promise) -> Unit): Reference<Cookies> {
    val cookies = Cookies()
    children.add(cookies)
    cookies.set(options, call)
    return cookies.refer()
}