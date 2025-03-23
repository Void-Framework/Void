package io.void.html.page

import io.void.dto.RequestDTO
import io.void.html.Element
import io.void.html.page.content.ContentType

abstract class Page(open val target: String) {

    lateinit var request: RequestDTO

    abstract fun content(): ContentType
}