package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Q(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "q") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.CITE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Q(vararg attribute: Attribute, _children: Element.() -> Unit): Q {
        val Q = Q(
            attributes = attribute,
            function = _children
        )
        children!!.add(Q)
        return Q
    }
