package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Meter(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "meter") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Meter(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Meter {
    val Meter =
        Meter(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Meter)
    return Meter
}
