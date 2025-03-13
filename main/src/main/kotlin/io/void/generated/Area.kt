package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Area(vararg attribute: Attribute): SelfClosingElement("area") {

    init {
        addAttributes(*attribute)
    }

    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.ALT, AttributeNames.COORDS, AttributeNames.SHAPE, AttributeNames.HREF, AttributeNames.TARGET)
}