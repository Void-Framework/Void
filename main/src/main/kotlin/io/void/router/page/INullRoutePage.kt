package io.void.router.page

import io.void.dto.Headers
import io.void.dto.RequestDTO

interface INullRoutePage {

    val request: RequestDTO
    val headers: Headers
    val statusText: String
}