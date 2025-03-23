package io.void.html.page.content

import io.void.dto.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page
import kotlin.reflect.KClass

sealed class ContentType {

    class htmlElements internal constructor(): ContentType() {
        lateinit var htmlElement: Element

        constructor(element: Element): this() {
            htmlElement = element
        }
    }
    class response internal constructor(): ContentType() {
        lateinit var response: ResponseDTO

        constructor(responseDTO: ResponseDTO): this() {
            response = responseDTO
        }
    }
}