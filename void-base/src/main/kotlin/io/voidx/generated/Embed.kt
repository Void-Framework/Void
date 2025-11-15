package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Embed(
    vararg attributes: Attribute,
) : SelfClosingElement("embed") {
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
