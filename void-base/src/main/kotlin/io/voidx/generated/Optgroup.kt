package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Optgroup(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "optgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Optgroup(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Optgroup {
    val Optgroup =
        Optgroup(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Optgroup)
    return Optgroup
}
