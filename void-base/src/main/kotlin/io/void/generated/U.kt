package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class U(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "u") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.U(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): U {
    val U =
        U(
            attributes = attribute,
            function = _children,
        )
    children!!.add(U)
    return U
}
