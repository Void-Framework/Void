package io.void.api

import io.void.api.method.Method
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page

abstract class ApiPage(override val target: String, val method: Method): Page(target = target) {

    override var content: Element? = null
    abstract fun serverGetter(request: RequestDTO): ResponseDTO
}