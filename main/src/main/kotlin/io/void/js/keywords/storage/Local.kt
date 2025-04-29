package io.void.js.keywords.storage

import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.asJsValue
import io.void.js.keywords.refer

internal interface Storage: Keyword {
    fun clear(): Reference<Storage> {
        jsReturn += ".clear()"
        return this.refer()
    }
    fun item(name: JsValue<String>, remove: Boolean = false): JsValue<String> {
        jsReturn += ".${if (remove) "remove" else "get"}Item($name)"
        return "".asJsValue()
    }
    fun item(name: JsValue<String>, value: JsValue<String>): Reference<Storage> {
        jsReturn += ".setItem($name, $value)"
        return this.refer()
    }
    fun length(): JsValue<Int> {
        jsReturn += ".length"
        return 0.asJsValue()
    }
    fun key(index: JsValue<Int>): JsValue<String> {
        jsReturn += ".key($index)"
        return "".asJsValue()
    }
}

class Local: Storage {
    override var jsReturn: String = ""
    override fun render(): String {
        return jsReturn
    }
}

class Session: Storage {
    override var jsReturn: String = ""
    override fun render(): String {
        return jsReturn
    }
}