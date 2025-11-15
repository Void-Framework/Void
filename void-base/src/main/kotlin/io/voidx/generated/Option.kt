package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Option(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "option") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Option(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Option {
    val Option =
        Option(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Option)
    return Option
}
