package io.void.html.page.content

import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.metadata.Metadata

sealed class ContentType {
    class HtmlElements internal constructor() : ContentType() {
        lateinit var htmlElement: Element
        lateinit var metadata: Metadata

        constructor(element: Element, metadata: Metadata) : this() {
            htmlElement = element
            this@HtmlElements.metadata = metadata
        }
    }

    class Response internal constructor() : ContentType() {
        lateinit var response: ResponseDTO

        constructor(responseDTO: ResponseDTO) : this() {
            response = responseDTO
        }
    }
}
