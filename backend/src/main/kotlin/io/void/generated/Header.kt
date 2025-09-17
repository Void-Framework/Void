package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Header(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "header") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

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
