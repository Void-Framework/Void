package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
