package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
