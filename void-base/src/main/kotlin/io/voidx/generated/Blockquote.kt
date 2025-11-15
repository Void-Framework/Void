package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Blockquote(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "blockquote") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Blockquote(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Blockquote {
    val Blockquote =
        Blockquote(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Blockquote)
    return Blockquote
}
