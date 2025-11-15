package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Li(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "li") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Li(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Li {
    val Li =
        Li(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Li)
    return Li
}
