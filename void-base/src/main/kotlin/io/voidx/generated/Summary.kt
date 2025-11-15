package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Summary(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "summary") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Summary(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Summary {
    val Summary =
        Summary(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Summary)
    return Summary
}
