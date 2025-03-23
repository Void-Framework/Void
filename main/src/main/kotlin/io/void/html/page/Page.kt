package io.void.html.page

import io.void.dto.RequestDTO
import io.void.html.Element
import io.void.html.page.content.ContentType
import kotlin.reflect.KClass

abstract class Page<T : ContentType>(open val target: String) {

    lateinit var request: RequestDTO
    abstract val contentType: KClass<T>

    abstract fun content(): T
}