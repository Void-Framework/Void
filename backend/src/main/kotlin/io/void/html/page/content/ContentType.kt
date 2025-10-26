package io.void.html.page.content

import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.metadata.Metadata

/**
 * Discriminated union describing what a Page produces.
 *
 * - [HtmlElements] wraps a root HTML [Element] and page [Metadata] to render.
 * - [Response] wraps a raw [ResponseDTO] for API-style endpoints.
 */
sealed class ContentType {
    /** HTML content consisting of a root [Element] and associated [Metadata]. */
    class HtmlElements internal constructor() : ContentType() {
        lateinit var htmlElement: Element
        lateinit var metadata: Metadata

        constructor(element: Element, metadata: Metadata) : this() {
            htmlElement = element
            this@HtmlElements.metadata = metadata
        }
    }

    /** Raw HTTP [ResponseDTO] to be sent as-is. */
    class Response internal constructor() : ContentType() {
        lateinit var response: ResponseDTO

        constructor(responseDTO: ResponseDTO) : this() {
            response = responseDTO
        }
    }
}
