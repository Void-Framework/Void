package main.html.element.content

import main.html.attributes.Attribute
import main.html.element.Element
import main.html.element.ElementWithChildren

class Div(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren("div") {

    init {
        this.apply(function)  // Apply the provided block to initialize the content
        attributes.forEach {
            set(it.name, it.value)
        }

    }
}

