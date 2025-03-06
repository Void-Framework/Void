package main.api

import main.html.page.Page
import main.java.main.DTO.RequestDTO
import main.java.main.DTO.ResponseDTO

abstract class ApiPage(override val target: String, val method: Method): Page(target = target) {

    abstract fun serverGetter(request: RequestDTO): ResponseDTO
}