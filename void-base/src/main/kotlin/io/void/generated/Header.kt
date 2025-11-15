package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Header(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "header") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Header(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Header {
    val Header =
        Header(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Header)
    return Header
}
