package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Dt(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "dt") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Dt(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Dt {
    val Dt =
        Dt(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Dt)
    return Dt
}
