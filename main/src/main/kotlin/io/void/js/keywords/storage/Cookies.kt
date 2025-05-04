package io.void.js.keywords.storage

import io.void.generated.Object
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.async.Promise
import io.void.js.keywords.datastructures.JsObject

class Cookies: Keyword {

    override var jsReturn: String = "cookieStore"
    override fun render(): String {
        return jsReturn
    }

    fun delete(name: JsValue<*>): Promise {
        jsReturn += ".delete($name)"
        return Promise()
    }
    fun get(name: JsValue<*>): Promise {
        jsReturn += ".get($name)"
        return Promise()
    }
    fun getAll(name: JsValue<*>): Promise {
        jsReturn += ".getAll($name)"
        return Promise()
    }
    fun getAll(): Promise {
        jsReturn += ".getAll()"
        return Promise()
    }
    fun set(name: JsValue<String>, value: JsValue<String>): Promise {
        jsReturn += ".set($name, $value)"
        return Promise()
    }
    fun set(options: JsValue<Object>): Promise {
        jsReturn += ".set($options)"
        return Promise()
    }
}