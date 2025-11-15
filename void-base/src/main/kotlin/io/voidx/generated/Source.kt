package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

class Source(
    vararg attributes: Attribute,
) : SelfClosingElement("source") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Source(vararg attribute: Attribute): Source {
    val Source =
        Source(
            attributes = attribute,
        )
    children!!.add(Source)
    return Source
}
