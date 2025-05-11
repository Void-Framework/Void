package io.void.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO

interface Middleware {

    val priority: Int

    fun processBefore(requestDTO: RequestDTO): ResponseDTO?
    fun processAfter(requestDTO: RequestDTO): ResponseDTO?
    fun handleError(e: Exception): ResponseDTO?
}