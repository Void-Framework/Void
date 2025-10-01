package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
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
