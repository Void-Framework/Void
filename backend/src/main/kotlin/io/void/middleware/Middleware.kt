package io.void.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO

interface Middleware {
    val priority: Int

    fun processBefore(requestDTO: RequestDTO): ResponseDTO?

    fun processAfter(requestDTO: RequestDTO): ResponseDTO?

    fun handleError(e: Exception): ResponseDTO?
}

fun middleware(priority: Int = 0, block: MiddlewareBuilder.() -> Unit): Middleware =
    object : Middleware {
        override val priority = priority
        private val builder = MiddlewareBuilder().apply(block)
        override fun processBefore(requestDTO: RequestDTO) = builder.before(requestDTO)
        override fun processAfter(requestDTO: RequestDTO) = builder.after(requestDTO)
        override fun handleError(e: Exception) = builder.onError(e)
    }

class MiddlewareBuilder {
    var before: (RequestDTO) -> ResponseDTO? = { null }
    var after: (RequestDTO) -> ResponseDTO? = { null }
    var onError: (Exception) -> ResponseDTO? = { null }
}
