package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Input(
    vararg attributes: Attribute,
) : SelfClosingElement("input") {
    override val allowedAttributes: List<AttributeNames> =
        listOf(
            AttributeNames.TYPE,
            AttributeNames.NAME,
            AttributeNames.VALUE,
            AttributeNames.PLACEHOLDER,
            AttributeNames.REQUIRED,
            AttributeNames.DISABLED,
            AttributeNames.READONLY,
            AttributeNames.SIZE,
            AttributeNames.MAXLENGTH,
            AttributeNames.MIN,
            AttributeNames.MAX,
            AttributeNames.STEP,
            AttributeNames.PATTERN,
            AttributeNames.AUTOCOMPLETE,
        )

    init {
        addAttributes(*attributes)
    }
}

fun Element.Input(vararg attribute: Attribute): Input {
    val Input =
        Input(
            attributes = attribute,
        )
    children!!.add(Input)
    return Input
}
