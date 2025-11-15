package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Rt(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "rt") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Rt(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Rt {
    val Rt =
        Rt(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Rt)
    return Rt
}
