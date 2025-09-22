package io.void.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO

interface Middleware {
    val priority: Int

    fun processBefore(requestDTO: Result<RequestDTO>): ResponseDTO?

    fun processAfter(requestDTO: Result<RequestDTO>): ResponseDTO?
}

fun middleware(
    priority: Int = 0,
    block: MiddlewareBuilder.() -> Unit,
): Middleware =
    object : Middleware {
        override val priority = priority
        private val builder = MiddlewareBuilder().apply(block)

        override fun processBefore(requestDTO: Result<RequestDTO>) = builder.before(requestDTO)

        override fun processAfter(requestDTO: Result<RequestDTO>) = builder.after(requestDTO)
    }

class MiddlewareBuilder {
    var before: (Result<RequestDTO>) -> ResponseDTO? = { null }
    var after: (Result<RequestDTO>) -> ResponseDTO? = { null }
}
