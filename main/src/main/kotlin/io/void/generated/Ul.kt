package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Ul(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "ul") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Li::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.Ul(vararg attribute: Attribute, _children: Element.() -> Unit): Ul {
        val Ul = Ul(
            attributes = attribute,
            function = _children
        )
        children!!.add(Ul)
        return Ul
    }
}