package io.void.js.async.promise

import io.void.dto.RequestDTO
import io.void.http.parser.HTTPParser
import io.void.js.BaseJSImplementation
import io.void.js.IJSFacade
import io.void.js.async.promise.exception.PromiseException
import java.io.InputStream

class Promise: IPromise {

    private val parser = HTTPParser()

    override fun then(response: IJSFacade.(RequestDTO) -> Unit, inputStream: InputStream): IPromise {
        try {
            val builtResponse = parser.parse(
                inputStream = inputStream
            )
            BaseJSImplementation.singleton.response(builtResponse)
            return this
        } catch (e: Exception) {
            return PromiseException(e)
        }
    }

    override fun catch(exception: (IJSFacade.(Exception) -> Unit)?, e: Exception) {
        if (exception != null) {
           PromiseException(e).catch(
               exception = exception,
               e = e
           )
        }
    }
}