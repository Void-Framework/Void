package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Col(vararg attribute: Attribute): SelfClosingElement("col") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SPAN)


    init {
        addAttributes(*attribute)
    }

}