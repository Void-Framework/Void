package main.api

import main.api.method.Method
import main.html.element.Element
import main.html.page.Page
import main.java.main.DTO.RequestDTO
import main.java.main.DTO.ResponseDTO

abstract class ApiPage(override val target: String, val method: Method): Page(target = target) {

    override var content: Element? = null
    abstract fun serverGetter(request: RequestDTO): ResponseDTO
}