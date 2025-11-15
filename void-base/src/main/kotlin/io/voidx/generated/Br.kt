package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Br(
    vararg attributes: Attribute,
) : SelfClosingElement("br") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Br(vararg attribute: Attribute): Br {
    val Br =
        Br(
            attributes = attribute,
        )
    children!!.add(Br)
    return Br
}
