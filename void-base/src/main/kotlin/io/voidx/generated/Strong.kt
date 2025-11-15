package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Strong(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "strong") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

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
