package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Tfoot(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "tfoot") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Tr::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Tfoot(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Tfoot {
    val Tfoot =
        Tfoot(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Tfoot)
    return Tfoot
}
