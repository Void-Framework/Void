package io.void.router.page

import io.void.dto.http.RequestDTO

interface INullRoutePage {
    val request: RequestDTO
    val headers: MutableMap<String, String>
    val statusText: String
}
