package io.void.html.page.content

import io.void.dto.ResponseDTO
import io.void.html.Element

sealed class ContentType {

    class HtmlElements internal constructor(): ContentType() {
        lateinit var htmlElement: Element

        constructor(element: Element): this() {
            htmlElement = element
        }
    }
    class Response internal constructor(): ContentType() {
        lateinit var response: ResponseDTO

        constructor(responseDTO: ResponseDTO): this() {
            response = responseDTO
        }
    }
}