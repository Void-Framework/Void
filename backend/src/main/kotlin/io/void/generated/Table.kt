package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Table(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "table") {
    override val acceptedChildren: MutableList<KClass<out Element>?> =
        mutableListOf(Caption::class, Colgroup::class, Thead::class, Tbody::class, Tfoot::class, Tr::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Table(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Table {
    val Table =
        Table(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Table)
    return Table
}
