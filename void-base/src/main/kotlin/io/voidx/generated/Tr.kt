package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Tr(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "tr") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Th::class, Td::class)

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
