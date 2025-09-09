package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Samp(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "samp") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Samp(vararg attribute: Attribute, _children: Element.() -> Unit): Samp {
        val Samp = Samp(
            attributes = attribute,
            function = _children
        )
        children!!.add(Samp)
        return Samp
    }
