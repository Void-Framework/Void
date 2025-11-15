package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Th(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "th") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Th(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Th {
    val Th =
        Th(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Th)
    return Th
}
