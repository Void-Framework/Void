package io.void.api

import io.void.html.Element
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

abstract class KtsPage(
    override val target: String
) : Page<ContentType.HtmlElements>(target) {

    override val contentType: KClass<ContentType.HtmlElements> = ContentType.HtmlElements::class
    override var metadata: Metadata? = null
    internal var _trigger: Element? = null
    internal var _target: Element? = null
    val trigger: Element?
        get() = _trigger
    val targetElement: Element?
        get() = _target
}