package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Track(vararg attribute: Attribute): SelfClosingElement("track") {

    init {
        addAttributes(*attribute)
    }

    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.DEFAULT, AttributeNames.KIND, AttributeNames.SRC, AttributeNames.SRCLANG, AttributeNames.LABEL)
}