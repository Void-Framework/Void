package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Dd(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "dd") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Dd(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Dd {
    val Dd =
        Dd(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Dd)
    return Dd
}
