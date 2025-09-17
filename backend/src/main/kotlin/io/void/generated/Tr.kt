package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Tr(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "tr") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Th::class, Td::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Tr(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Tr {
    val Tr =
        Tr(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Tr)
    return Tr
}
