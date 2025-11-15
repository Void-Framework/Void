package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Div(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "div") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Div(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Div {
    val Div =
        Div(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Div)
    return Div
}
