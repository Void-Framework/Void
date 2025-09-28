package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Input(vararg attributes: Attribute): SelfClosingElement("input") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Input(vararg attribute: Attribute): Input {
        val Input = Input(
            attributes = attribute
        )
        children!!.add(Input)
        return Input
    }
