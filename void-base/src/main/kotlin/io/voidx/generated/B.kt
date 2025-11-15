package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class B(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "b") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.B(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): B {
    val B =
        B(
            attributes = attribute,
            function = _children,
        )
    children!!.add(B)
    return B
}
