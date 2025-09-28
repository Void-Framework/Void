package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Hr(vararg attributes: Attribute): SelfClosingElement("hr") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Hr(vararg attribute: Attribute): Hr {
        val Hr = Hr(
            attributes = attribute
        )
        children!!.add(Hr)
        return Hr
    }
