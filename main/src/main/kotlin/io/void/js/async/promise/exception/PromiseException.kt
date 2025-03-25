package io.void.js.async.promise.exception

import io.void.dto.RequestDTO
import io.void.js.BaseJSImplementation
import io.void.js.IJSFacade
import io.void.js.async.promise.IPromise
import java.io.InputStream

class PromiseException(private val e: Exception): Exception(e), IPromise {

    override fun then(response: IJSFacade.(RequestDTO) -> Unit, inputStream: InputStream): IPromise {
        throw UnsupportedOperationException("Cannot process data after the raised error: ${e.stackTrace}")
    }

    override fun catch(exception: (IJSFacade.(Exception) -> Unit)?, e: Exception) {
        if (exception != null) {
            BaseJSImplementation.singleton.exception(e)
        }
    }
}