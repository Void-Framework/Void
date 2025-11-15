package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Area(
    vararg attributes: Attribute,
) : SelfClosingElement("area") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Area(vararg attribute: Attribute): Area {
    val Area =
        Area(
            attributes = attribute,
        )
    children!!.add(Area)
    return Area
}
