package io.void.html.page.content

import io.void.dto.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page
import kotlin.reflect.KClass

sealed class ContentType {

    data class htmlElements(val element: Element): ContentType()
    data class response(val responseDTO: ResponseDTO): ContentType()
}