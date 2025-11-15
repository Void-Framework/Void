package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Ol(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "ol") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Li::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Ol(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Ol {
    val Ol =
        Ol(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Ol)
    return Ol
}
