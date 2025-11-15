package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
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
