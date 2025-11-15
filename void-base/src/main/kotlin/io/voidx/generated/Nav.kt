package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
