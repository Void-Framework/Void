package io.void.js.async.promise

import io.void.dto.RequestDTO
import io.void.js.IJSFacade
import java.io.InputStream

interface IPromise {

    fun then(response: IJSFacade.(RequestDTO) -> Unit, inputStream: InputStream): IPromise
    fun catch(exception: (IJSFacade.(Exception) -> Unit)? = null, e: Exception)
}