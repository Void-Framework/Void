package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Table(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "table") {
    override val acceptedChildren: MutableList<KClass<out Element>?> =
        mutableListOf(Caption::class, Colgroup::class, Thead::class, Tbody::class, Tfoot::class, Tr::class)

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
