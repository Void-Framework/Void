package io.jadiefication.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.middleware.Middleware

class LogMiddleware: Middleware {

    override val priority: Int = 0

    override fun processBefore(requestDTO: RequestDTO): ResponseDTO? {
        return null
    }

    override fun processAfter(requestDTO: RequestDTO): ResponseDTO? {
        println(requestDTO)
        return null
    }

    override fun handleError(e: Exception): ResponseDTO? {
        println(e)
        return null
    }
}