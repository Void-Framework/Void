package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Cite(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "cite") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Cite(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Cite {
    val Cite =
        Cite(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Cite)
    return Cite
}
