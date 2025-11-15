package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Map(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "map") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Area::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Map(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Map {
    val Map =
        Map(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Map)
    return Map
}
