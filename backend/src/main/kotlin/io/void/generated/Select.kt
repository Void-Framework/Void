package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Select(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "select") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class, Optgroup::class)
    override val allowedAttributes: List<AttributeNames> =
        listOf(AttributeNames.NAME, AttributeNames.REQUIRED, AttributeNames.DISABLED, AttributeNames.MULTIPLE, AttributeNames.SIZE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Select(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Select {
    val Select =
        Select(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Select)
    return Select
}
