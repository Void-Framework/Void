package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Aside(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "aside") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Aside(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Aside {
    val Aside =
        Aside(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Aside)
    return Aside
}
