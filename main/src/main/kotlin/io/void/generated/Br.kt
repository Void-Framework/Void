package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Br(vararg attributes: Attribute): SelfClosingElement("br") {
    override val allowedAttributes: List<AttributeNames> = listOf()


    init {
        addAttributes(*attributes)
    }

    fun Element.BR(vararg attribute: Attribute): Br {
        val BR = Br(
            attributes = attribute
        )
        children!!.add(BR)
        return BR
    }
}