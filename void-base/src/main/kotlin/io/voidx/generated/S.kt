package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class S(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "s") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.S(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): S {
    val S =
        S(
            attributes = attribute,
            function = _children,
        )
    children!!.add(S)
    return S
}
