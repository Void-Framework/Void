package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Menu(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "menu") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Menuitem::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Menu(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Menu {
    val Menu =
        Menu(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Menu)
    return Menu
}
