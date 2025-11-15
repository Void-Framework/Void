package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Dfn(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "dfn") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Dfn(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Dfn {
    val Dfn =
        Dfn(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Dfn)
    return Dfn
}
