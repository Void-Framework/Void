package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

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
