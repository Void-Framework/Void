package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Input(vararg attribute: Attribute): SelfClosingElement("input") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.TYPE, AttributeNames.NAME, AttributeNames.VALUE, AttributeNames.PLACEHOLDER, AttributeNames.REQUIRED, AttributeNames.DISABLED, AttributeNames.READONLY, AttributeNames.SIZE, AttributeNames.MAXLENGTH, AttributeNames.MIN, AttributeNames.MAX, AttributeNames.STEP, AttributeNames.PATTERN, AttributeNames.AUTOCOMPLETE)


    init {
        addAttributes(*attribute)
    }

}