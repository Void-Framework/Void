package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Li(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "li") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.LI(vararg attribute: Attribute, _children: Element.() -> Unit): Li {
        val LI = Li(
            attributes = attribute,
            function = _children
        )
        children!!.add(LI)
        return LI
    }
}