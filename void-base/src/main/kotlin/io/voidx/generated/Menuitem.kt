package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

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
