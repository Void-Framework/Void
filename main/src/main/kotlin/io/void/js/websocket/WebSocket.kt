package io.void.js.websocket

import io.void.js.JavaScript
import io.void.js.keywords.BrowserObject
import io.void.js.keywords.JsValue
import io.void.js.keywords.Reference
import io.void.js.keywords.datastructures.JsList

class WebSocket : BrowserObject {

    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    fun create(url: JsValue<String>, call: (WebSocket) -> Unit): Reference<WebSocket> {
        jsReturn = "new WebSocket($url)"
        return applyMethods(call, WebSocket(), this)
    }

    fun close(code: JsValue<Int>? = null, reason: JsValue<String>? = null) {
        jsReturn += ".close(${if (code != null) "$code" else ""}${if (code != null && reason != null) ", $reason" else if (reason != null) "$reason" else ""})"
    }

    fun <T> send(data: T) where T : JsValue<String>, T : JsList<*> {
        jsReturn += ".send($data)"
    }
}

fun JavaScript.create(url: JsValue<String>, call: (WebSocket) -> Unit): Reference<WebSocket> {
    val socket = WebSocket()
    val result = socket.create(url, call)
    children.add(socket)
    return result
}