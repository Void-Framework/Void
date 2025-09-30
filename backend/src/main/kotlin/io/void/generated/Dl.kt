package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Dl(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "dl") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Dt::class, Dd::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Dl(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Dl {
    val Dl =
        Dl(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Dl)
    return Dl
}
