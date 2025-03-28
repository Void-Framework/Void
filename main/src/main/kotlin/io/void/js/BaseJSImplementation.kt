package io.void.js

import io.void.dto.RequestDTO
import io.void.js.async.promise.Promise
import java.net.URL

class BaseJSImplementation private constructor(): IJSFacade {

    companion object {
        val singleton = BaseJSImplementation()
    }

    override fun redirect(url: URL, reload: Boolean, jsonData: String?) {

    }

    override fun popup(message: String, type: PopupType) {

    }

    fun prompt(message: String, defaultValue: String): String {
        return ""
    }

    override fun fetch(url: URL, request: RequestDTO): Promise {
        return Promise()
    }
}