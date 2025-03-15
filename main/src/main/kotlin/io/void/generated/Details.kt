package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Details(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "details") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.OPEN)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.Details(vararg attribute: Attribute, _children: Element.() -> Unit): Details {
        val Details = Details(
            attributes = attribute,
            function = _children
        )
        children!!.add(Details)
        return Details
    }
}