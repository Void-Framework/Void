package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Strong(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "strong") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Strong(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Strong {
    val Strong =
        Strong(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Strong)
    return Strong
}
