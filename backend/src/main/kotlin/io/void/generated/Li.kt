package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Li(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "li") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Li(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Li {
    val Li =
        Li(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Li)
    return Li
}
