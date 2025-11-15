package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Figcaption(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "figcaption") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Figcaption(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Figcaption {
    val Figcaption =
        Figcaption(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Figcaption)
    return Figcaption
}
