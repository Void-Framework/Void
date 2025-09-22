package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Embed(
    vararg attributes: Attribute,
) : SelfClosingElement("embed") {
    override val allowedAttributes: List<AttributeNames> =
        listOf(AttributeNames.SRC, AttributeNames.TYPE, AttributeNames.WIDTH, AttributeNames.HEIGHT)

    init {
        addAttributes(*attributes)
    }
}

fun Element.Embed(vararg attribute: Attribute): Embed {
    val Embed =
        Embed(
            attributes = attribute,
        )
    children!!.add(Embed)
    return Embed
}
