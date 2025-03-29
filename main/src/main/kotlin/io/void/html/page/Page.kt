package io.void.html.page

import io.void.dto.http.RequestDTO
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

abstract class Page<T : ContentType>(open val target: String) {

    lateinit var request: RequestDTO
    abstract val contentType: KClass<T>
    abstract val metadata: Metadata?

    abstract fun content(): T
}