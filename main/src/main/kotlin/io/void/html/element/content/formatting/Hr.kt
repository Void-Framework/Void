package io.void.html.element.content.formatting

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.html.element.SelfClosingElement

class Hr(vararg attribute: Attribute): SelfClosingElement("hr") {

    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attribute)
    }
}