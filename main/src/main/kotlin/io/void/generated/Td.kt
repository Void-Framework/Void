package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Td(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "td") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.COLSPAN, AttributeNames.ROWSPAN, AttributeNames.HEADERS)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.TD(vararg attribute: Attribute, _children: Element.() -> Unit): Td {
        val TD = Td(
            attributes = attribute,
            function = _children
        )
        children!!.add(TD)
        return TD
    }
}