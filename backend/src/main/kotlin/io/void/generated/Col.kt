package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Col(vararg attributes: Attribute): SelfClosingElement("col") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SPAN)


    init {
        addAttributes(*attributes)
    }

}    fun Element.Col(vararg attribute: Attribute): Col {
        val Col = Col(
            attributes = attribute
        )
        children!!.add(Col)
        return Col
    }
