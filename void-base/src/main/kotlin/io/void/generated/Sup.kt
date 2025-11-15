package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Sup(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "sup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Sup(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Sup {
    val Sup =
        Sup(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Sup)
    return Sup
}
