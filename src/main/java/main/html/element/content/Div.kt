package main.html.element.content

import main.html.attributes.Attribute
import main.html.attributes.AttributeNames
import main.html.element.Element
import main.html.element.ElementWithChildren

class Div(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren("div") {

    override val allowedAttributes: List<AttributeNames>
        get() = TODO("Not yet implemented")

    init {
        this.apply(function)  // Apply the provided block to initialize the content
        attributes.forEach {
            if (isAllowed(it.name) && it.isCorrectValue()) {
                set(it.name.name.lowercase(), it.value.toString())
            }
        }

    }
}

