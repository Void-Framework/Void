package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Code(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "code") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Code(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Code {
    val Code =
        Code(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Code)
    return Code
}
