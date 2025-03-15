package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Th(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "th") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.COLSPAN, AttributeNames.ROWSPAN, AttributeNames.HEADERS, AttributeNames.SCOPE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Th(vararg attribute: Attribute, _children: Element.() -> Unit): Th {
        val Th = Th(
            attributes = attribute,
            function = _children
        )
        children!!.add(Th)
        return Th
    }
