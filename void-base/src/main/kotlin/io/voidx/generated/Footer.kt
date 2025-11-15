package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Footer(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "footer") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Footer(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Footer {
    val Footer =
        Footer(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Footer)
    return Footer
}
