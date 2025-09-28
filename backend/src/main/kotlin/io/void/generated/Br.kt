package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Br(vararg attributes: Attribute): SelfClosingElement("br") {

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
