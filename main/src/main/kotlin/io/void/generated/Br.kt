package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Br(vararg attribute: Attribute): SelfClosingElement("br") {
    override val allowedAttributes: List<AttributeNames> = listOf()


    init {
        addAttributes(*attribute)
    }

}