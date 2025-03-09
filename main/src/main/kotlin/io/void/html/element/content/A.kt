package io.void.html.element.content

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.html.element.Element
import io.void.html.element.ElementWithChildren

class A(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "a") {

    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.DOWNLOAD, AttributeNames.HREF,
        AttributeNames.HREFLANG, AttributeNames.MEDIA, AttributeNames.PING, AttributeNames.REFERERPOLICY,
        AttributeNames.REL, AttributeNames.TARGET, AttributeNames.TYPE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}