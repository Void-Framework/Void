package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Img(
    vararg attributes: Attribute,
) : SelfClosingElement("img") {
    override val allowedAttributes: List<AttributeNames> =
        listOf(
            AttributeNames.ALT,
            AttributeNames.CROSSORIGIN,
            AttributeNames.HEIGHT,
            AttributeNames.ISMAP,
            AttributeNames.LOADING,
            AttributeNames.REFERERPOLICY,
            AttributeNames.SIZES,
            AttributeNames.SRC,
            AttributeNames.SRCSET,
            AttributeNames.USEMAP,
            AttributeNames.WIDTH,
        )

    init {
        addAttributes(*attributes)
    }
}

fun Element.Img(vararg attribute: Attribute): Img {
    val Img =
        Img(
            attributes = attribute,
        )
    children!!.add(Img)
    return Img
}
