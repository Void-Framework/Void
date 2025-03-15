package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Tr(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "tr") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Th::class, Td::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.TR(vararg attribute: Attribute, _children: Element.() -> Unit): Tr {
        val TR = Tr(
            attributes = attribute,
            function = _children
        )
        children!!.add(TR)
        return TR
    }
}