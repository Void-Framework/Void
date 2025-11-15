package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Mark(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "mark") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Mark(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Mark {
    val Mark =
        Mark(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Mark)
    return Mark
}
