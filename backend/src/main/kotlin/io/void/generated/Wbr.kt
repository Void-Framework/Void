package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Wbr(vararg attributes: Attribute): SelfClosingElement("wbr") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Wbr(vararg attribute: Attribute): Wbr {
        val Wbr = Wbr(
            attributes = attribute
        )
        children!!.add(Wbr)
        return Wbr
    }
