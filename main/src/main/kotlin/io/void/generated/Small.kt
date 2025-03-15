package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Small(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "small") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.SMALL(vararg attribute: Attribute, _children: Element.() -> Unit): Small {
        val SMALL = Small(
            attributes = attribute,
            function = _children
        )
        children!!.add(SMALL)
        return SMALL
    }
}