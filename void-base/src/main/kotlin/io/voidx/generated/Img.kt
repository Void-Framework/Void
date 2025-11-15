package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Img(
    vararg attributes: Attribute,
) : SelfClosingElement("img") {
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
