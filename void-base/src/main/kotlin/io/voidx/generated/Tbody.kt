package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Tbody(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "tbody") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Tr::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Tbody(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Tbody {
    val Tbody =
        Tbody(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Tbody)
    return Tbody
}
