package io.void.js.async.promise

import io.void.dto.RequestDTO
import io.void.js.BaseJSImplementation
import java.io.InputStream

interface IPromise {

    fun then(response: BaseJSImplementation.Companion.(RequestDTO) -> Unit, inputStream: InputStream): IPromise
    fun catch(exception: (BaseJSImplementation.Companion.(Exception) -> Unit)? = null, e: Exception)
}