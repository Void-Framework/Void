package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Thead(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "thead") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Tr::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Thead(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Thead {
    val Thead =
        Thead(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Thead)
    return Thead
}
