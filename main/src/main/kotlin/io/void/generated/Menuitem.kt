package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Menuitem(vararg attribute: Attribute): SelfClosingElement("menuitem") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.TYPE, AttributeNames.LABEL, AttributeNames.CHECKED, AttributeNames.DISABLED)


    init {
        addAttributes(*attribute)
    }

}