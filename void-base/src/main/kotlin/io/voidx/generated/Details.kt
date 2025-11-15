package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Details(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "details") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Details(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Details {
    val Details =
        Details(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Details)
    return Details
}
