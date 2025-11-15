package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Td(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "td") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Td(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Td {
    val Td =
        Td(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Td)
    return Td
}
