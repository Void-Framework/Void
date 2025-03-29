package io.void.js

import io.void.dto.RequestDTO
import io.void.js.async.promise.Promise
import java.net.URL

abstract class BaseJSImplementation {

    companion object {
        fun redirect(url: URL, reload: Boolean, jsonData: String?) {

        }

        fun popup(message: String, type: PopupType) {

        }

        fun prompt(message: String, defaultValue: String): String {
            return ""
        }

        fun fetch(url: URL, request: RequestDTO): Promise {
            return Promise()
        }
    }
}