package io.void.api

import io.void.dto.http.RequestDTO
import io.void.html.Element
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import kotlin.reflect.KClass

abstract class KtsPage(
    override val target: String,
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

fun ktsRoute(
    path: String,
    block: KtsPage.(RequestDTO, Element?, Element?) -> Element,
): KtsPage =
    object : KtsPage(target = path) {
        override val contentType = ContentType.HtmlElements::class

        override fun content() = ContentType.HtmlElements(block(request, trigger, targetElement), metadata(this) {})
    }
