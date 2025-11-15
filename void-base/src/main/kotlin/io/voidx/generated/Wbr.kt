package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Wbr(
    vararg attributes: Attribute,
) : SelfClosingElement("wbr") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Wbr(vararg attribute: Attribute): Wbr {
    val Wbr =
        Wbr(
            attributes = attribute,
        )
    children!!.add(Wbr)
    return Wbr
}
