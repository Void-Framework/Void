package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Br(vararg attributes: Attribute): SelfClosingElement("br") {
    override val allowedAttributes: List<AttributeNames> = listOf()


    init {
        addAttributes(*attributes)
    }

}    fun Element.Br(vararg attribute: Attribute): Br {
        val Br = Br(
            attributes = attribute
        )
        children!!.add(Br)
        return Br
    }
