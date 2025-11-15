package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Input(
    vararg attributes: Attribute,
) : SelfClosingElement("input") {
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
