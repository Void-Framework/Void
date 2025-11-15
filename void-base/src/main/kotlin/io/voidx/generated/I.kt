package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class I(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "i") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.I(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): I {
    val I =
        I(
            attributes = attribute,
            function = _children,
        )
    children!!.add(I)
    return I
}
