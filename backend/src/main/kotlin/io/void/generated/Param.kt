package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Param(vararg attributes: Attribute): SelfClosingElement("param") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Param(vararg attribute: Attribute): Param {
        val Param = Param(
            attributes = attribute
        )
        children!!.add(Param)
        return Param
    }
