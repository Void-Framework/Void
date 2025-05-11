package io.void.html.page

import io.void.dto.http.RequestDTO
import io.void.html.Element
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

abstract class Page<T : ContentType>(open val target: String) {

    val classAttributes: MutableMap<Element, List<String>> = mutableMapOf()

    lateinit var request: RequestDTO
    abstract val contentType: KClass<T>
    abstract var metadata: Metadata?

    abstract fun content(): T
}