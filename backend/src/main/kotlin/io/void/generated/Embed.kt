package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Embed(vararg attributes: Attribute): SelfClosingElement("embed") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Embed(vararg attribute: Attribute): Embed {
        val Embed = Embed(
            attributes = attribute
        )
        children!!.add(Embed)
        return Embed
    }
