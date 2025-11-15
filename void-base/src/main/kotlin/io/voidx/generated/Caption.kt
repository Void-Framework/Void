package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Caption(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "caption") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Caption(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Caption {
    val Caption =
        Caption(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Caption)
    return Caption
}
