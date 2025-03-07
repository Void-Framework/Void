package main.html.element.content.formatting

import main.html.attributes.AttributeNames
import main.html.element.SelfClosingElement
import main.html.element.content.InlineElement

class Br: SelfClosingElement("br"), InlineElement {

    override val allowedAttributes: List<AttributeNames> = listOf()
}
