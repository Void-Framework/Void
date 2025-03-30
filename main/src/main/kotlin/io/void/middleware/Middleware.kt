package io.void.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO

interface Middleware {

    fun process(requestDTO: RequestDTO): ResponseDTO?
}