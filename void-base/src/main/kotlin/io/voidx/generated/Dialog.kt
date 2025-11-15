package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Dialog(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "dialog") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Dialog(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Dialog {
    val Dialog =
        Dialog(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Dialog)
    return Dialog
}
