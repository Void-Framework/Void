package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Source(vararg attributes: Attribute): SelfClosingElement("source") {

    init {
        addAttributes(*attributes)
    }

}    fun Element.Source(vararg attribute: Attribute): Source {
        val Source = Source(
            attributes = attribute
        )
        children!!.add(Source)
        return Source
    }
