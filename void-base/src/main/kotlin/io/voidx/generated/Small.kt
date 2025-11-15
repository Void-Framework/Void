package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Small(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "small") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Small(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Small {
    val Small =
        Small(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Small)
    return Small
}
