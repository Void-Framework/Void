package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Hr(
    vararg attributes: Attribute,
) : SelfClosingElement("hr") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Hr(vararg attribute: Attribute): Hr {
    val Hr =
        Hr(
            attributes = attribute,
        )
    children!!.add(Hr)
    return Hr
}
