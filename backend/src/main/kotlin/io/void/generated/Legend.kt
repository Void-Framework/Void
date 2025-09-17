package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Legend(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "legend") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Legend(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Legend {
    val Legend =
        Legend(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Legend)
    return Legend
}
