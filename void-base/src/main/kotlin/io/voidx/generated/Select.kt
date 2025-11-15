package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Select(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "select") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class, Optgroup::class)

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
