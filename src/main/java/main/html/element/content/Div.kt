package main.html.element.content

import main.html.attributes.Attribute
import main.html.attributes.AttributeNames
import main.html.element.Element
import main.html.element.ElementWithChildren

class Div(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren("div") {

    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

