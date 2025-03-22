package io.void.js

import io.void.dto.RequestDTO
import io.void.js.async.promise.Promise
import java.net.URL

class BaseJSImplementation private constructor(): IJSFacade {

    val js = StringBuilder("")

    companion object {
        val singleton = BaseJSImplementation()
    }

    override fun redirect(url: URL, reload: Boolean, jsonData: String?) {
        js.append("${
            if (reload) {
                "window.location.href = \"${url.path}\""
            } else {
                "history.pushState({ ${
                    if (jsonData == null) {
                        ""
                    } else {
                        "$jsonData"
                    }
                }, \"${url.path}\")"
            }
        };")
    }

    override fun popup(message: String, type: PopupType): String? {
        TODO("Not yet implemented")
    }

    override fun fetch(url: URL, request: RequestDTO): Promise {
        TODO("Not yet implemented")
    }
}