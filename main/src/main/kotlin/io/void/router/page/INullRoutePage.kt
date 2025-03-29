package io.void.router.page

import io.void.dto.http.Headers
import io.void.dto.http.RequestDTO

interface INullRoutePage {

    val request: RequestDTO
    val headers: Headers
    val statusText: String
}