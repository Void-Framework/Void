package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Summary(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "summary") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Summary(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Summary {
    val Summary =
        Summary(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Summary)
    return Summary
}
