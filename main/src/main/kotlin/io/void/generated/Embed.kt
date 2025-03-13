package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Embed(vararg attribute: Attribute): SelfClosingElement("embed") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SRC, AttributeNames.TYPE, AttributeNames.WIDTH, AttributeNames.HEIGHT)


    init {
        addAttributes(*attribute)
    }

}