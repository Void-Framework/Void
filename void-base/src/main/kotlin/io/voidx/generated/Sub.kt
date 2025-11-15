package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
