package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Nav(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "nav") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Nav(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Nav {
    val Nav =
        Nav(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Nav)
    return Nav
}
