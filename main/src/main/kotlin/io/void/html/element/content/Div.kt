package io.void.html.element.content

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.html.element.Element
import io.void.html.element.ElementWithChildren
import kotlin.reflect.KClass

class Div(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren("div") {

    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf()

    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}
