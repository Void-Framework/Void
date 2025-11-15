package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Param(
    vararg attributes: Attribute,
) : SelfClosingElement("param") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Param(vararg attribute: Attribute): Param {
    val Param =
        Param(
            attributes = attribute,
        )
    children!!.add(Param)
    return Param
}
