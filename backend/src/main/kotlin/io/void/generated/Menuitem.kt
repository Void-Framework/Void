package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Menuitem(
    vararg attributes: Attribute,
) : SelfClosingElement("menuitem") {
    override val allowedAttributes: List<AttributeNames> =
        listOf(AttributeNames.TYPE, AttributeNames.LABEL, AttributeNames.CHECKED, AttributeNames.DISABLED)

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
