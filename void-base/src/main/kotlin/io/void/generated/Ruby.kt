package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Ruby(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "ruby") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Ruby(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Ruby {
    val Ruby =
        Ruby(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Ruby)
    return Ruby
}
