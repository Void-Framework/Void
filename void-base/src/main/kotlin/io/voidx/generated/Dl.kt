package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
