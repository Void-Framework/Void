package io.void.html.page

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import kotlin.reflect.KClass

abstract class Page<T : ContentType>(
    open val target: String,
) {
    val classAttributes: MutableMap<Element, List<String>> = mutableMapOf()

    lateinit var request: RequestDTO
    abstract val contentType: KClass<T>
    abstract var metadata: Metadata?

    abstract fun content(): T
}

fun htmlRoute(path: String, metadata: Metadata.() -> Unit, block: ContentType.HtmlElements.() -> Unit): Page<ContentType.HtmlElements> =
    object : Page<ContentType.HtmlElements>(target = path) {
        private val _metadata = metadata(this) {  }.apply(metadata)
        override var metadata: Metadata? = _metadata
        override val contentType = ContentType.HtmlElements::class
        override fun content() = ContentType.HtmlElements().apply(block)
    }

fun jsonRoute(path: String, block: () -> ResponseDTO): Page<ContentType.Response> =
    object : Page<ContentType.Response>(target = path) {
        override var metadata: Metadata? = null
        override val contentType = ContentType.Response::class
        override fun content() = ContentType.Response(block())
    }

