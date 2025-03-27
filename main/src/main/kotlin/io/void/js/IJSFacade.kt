package io.void.js

import io.void.dto.RequestDTO
import io.void.js.async.promise.Promise
import java.net.URL

interface IJSFacade {

    fun redirect(url: URL, reload: Boolean = true, jsonData: String? = null)
    fun popup(message: String, type: PopupType)
    fun fetch(url: URL, request: RequestDTO): Promise
}
