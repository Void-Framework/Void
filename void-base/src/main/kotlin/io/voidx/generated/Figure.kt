package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Figure(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "figure") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Figure(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Figure {
    val Figure =
        Figure(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Figure)
    return Figure
}
