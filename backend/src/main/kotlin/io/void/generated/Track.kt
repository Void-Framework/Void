package io.void.generated

import io.void.html.Element
import io.void.html.SelfClosingElement
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

class Track(vararg attributes: Attribute): SelfClosingElement("track") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.DEFAULT, AttributeNames.KIND, AttributeNames.SRC, AttributeNames.SRCLANG, AttributeNames.LABEL)


    init {
        addAttributes(*attributes)
    }

}    fun Element.Track(vararg attribute: Attribute): Track {
        val Track = Track(
            attributes = attribute
        )
        children!!.add(Track)
        return Track
    }
