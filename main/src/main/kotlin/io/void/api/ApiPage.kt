package io.void.api

import io.void.api.method.Method
import io.void.html.element.Element
import io.void.html.page.Page
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO

abstract class ApiPage(override val target: String, val method: Method): Page(target = target) {

    override var content: Element? = null
    abstract fun serverGetter(request: RequestDTO): ResponseDTO
}