package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Menuitem(
    vararg attributes: Attribute,
) : SelfClosingElement("menuitem") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Menuitem(vararg attribute: Attribute): Menuitem {
    val Menuitem =
        Menuitem(
            attributes = attribute,
        )
    children!!.add(Menuitem)
    return Menuitem
}
