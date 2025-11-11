package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Colgroup(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "colgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Col::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Colgroup(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Colgroup {
    val Colgroup =
        Colgroup(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Colgroup)
    return Colgroup
}
