package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Textarea(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "textarea") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Textarea(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Textarea {
    val Textarea =
        Textarea(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Textarea)
    return Textarea
}
