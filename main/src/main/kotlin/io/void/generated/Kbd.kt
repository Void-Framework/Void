package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Kbd(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "kbd") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.KBD(vararg attribute: Attribute, _children: Element.() -> Unit): Kbd {
        val KBD = Kbd(
            attributes = attribute,
            function = _children
        )
        children!!.add(KBD)
        return KBD
    }
}