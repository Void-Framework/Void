package io.void.js

import io.void.dto.RequestDTO
import io.void.js.async.promise.Promise
import java.net.URL

class BaseJSImplementation private constructor(): IJSFacade {

    val js = StringBuilder("")

    companion object {
        val singleton = BaseJSImplementation()
    }

    external fun redirect0(url: URL, reload: Boolean, jsonData: String?)
    external fun popup0(message: String, type: PopupType)
    external fun prompt0(message: String, defaultValue: String): String
    external fun fetch0(url: URL, request: RequestDTO): Promise

    override fun redirect(url: URL, reload: Boolean, jsonData: String?) {
        redirect0(url, reload, jsonData)
    }

    override fun popup(message: String, type: PopupType) {
        popup0(message, type)
    }

    fun prompt(message: String, defaultValue: String): String {
        return prompt0(message, defaultValue)
    }

    override fun fetch(url: URL, request: RequestDTO): Promise {
        return fetch0(url, request)
    }
}