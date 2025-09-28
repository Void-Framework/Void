package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Img(vararg attributes: Attribute): SelfClosingElement("img") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Img(vararg attribute: Attribute): Img {
        val Img = Img(
            attributes = attribute
        )
        children!!.add(Img)
        return Img
    }
