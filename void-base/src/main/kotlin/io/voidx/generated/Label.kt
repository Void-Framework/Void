package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Label(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "label") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Label(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Label {
    val Label =
        Label(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Label)
    return Label
}
