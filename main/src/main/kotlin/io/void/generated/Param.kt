package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Param(vararg attribute: Attribute): SelfClosingElement("param") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.NAME, AttributeNames.VALUE)


    init {
        addAttributes(*attribute)
    }

}