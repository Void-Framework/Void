package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Sub(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "sub") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Sub(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Sub {
    val Sub =
        Sub(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Sub)
    return Sub
}
