package main.html.element.content

import main.html.attributes.Attribute
import main.html.attributes.AttributeNames
import main.html.element.Element
import main.html.element.ElementWithChildren

class A(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "a"), InlineElement {

    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.DOWNLOAD, AttributeNames.HREF,
        AttributeNames.HREFLANG, AttributeNames.MEDIA, AttributeNames.PING, AttributeNames.REFERERPOLICY,
        AttributeNames.REL, AttributeNames.TARGET, AttributeNames.TYPE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}