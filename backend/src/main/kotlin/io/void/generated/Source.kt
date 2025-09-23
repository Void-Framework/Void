package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Source(
    vararg attributes: Attribute,
) : SelfClosingElement("source") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SRC, AttributeNames.TYPE, AttributeNames.MEDIA)

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
