package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Optgroup(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "optgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.LABEL, AttributeNames.DISABLED)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Optgroup(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Optgroup {
    val Optgroup =
        Optgroup(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Optgroup)
    return Optgroup
}
