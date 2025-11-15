package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Abbr(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "abbr") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Abbr(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Abbr {
    val Abbr =
        Abbr(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Abbr)
    return Abbr
}
