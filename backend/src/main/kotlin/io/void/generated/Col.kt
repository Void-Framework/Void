package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Col(vararg attributes: Attribute): SelfClosingElement("col") {

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
