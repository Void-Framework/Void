package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Aside(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "aside") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.ASIDE(vararg attribute: Attribute, _children: Element.() -> Unit): Aside {
        val ASIDE = Aside(
            attributes = attribute,
            function = _children
        )
        children!!.add(ASIDE)
        return ASIDE
    }
}