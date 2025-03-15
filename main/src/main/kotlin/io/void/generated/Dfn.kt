package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Dfn(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "dfn") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.DFN(vararg attribute: Attribute, _children: Element.() -> Unit): Dfn {
        val DFN = Dfn(
            attributes = attribute,
            function = _children
        )
        children!!.add(DFN)
        return DFN
    }
}