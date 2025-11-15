package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Samp(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "samp") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Samp(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Samp {
    val Samp =
        Samp(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Samp)
    return Samp
}
