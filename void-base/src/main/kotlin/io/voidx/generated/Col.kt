package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Col(
    vararg attributes: Attribute,
) : SelfClosingElement("col") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Col(vararg attribute: Attribute): Col {
    val Col =
        Col(
            attributes = attribute,
        )
    children!!.add(Col)
    return Col
}
