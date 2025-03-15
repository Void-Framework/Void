package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Menuitem(vararg attributes: Attribute): SelfClosingElement("menuitem") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.TYPE, AttributeNames.LABEL, AttributeNames.CHECKED, AttributeNames.DISABLED)


    init {
        addAttributes(*attributes)
    }

    fun Element.MENUITEM(vararg attribute: Attribute): Menuitem {
        val MENUITEM = Menuitem(
            attributes = attribute
        )
        children!!.add(MENUITEM)
        return MENUITEM
    }
}