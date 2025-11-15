package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Button(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "button") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Button(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Button {
    val Button =
        Button(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Button)
    return Button
}
