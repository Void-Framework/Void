package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Ins(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "ins") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Ins(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Ins {
    val Ins =
        Ins(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Ins)
    return Ins
}
