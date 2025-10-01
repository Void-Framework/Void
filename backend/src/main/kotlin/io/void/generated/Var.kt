package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Var(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "var") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Var(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Var {
    val Var =
        Var(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Var)
    return Var
}
