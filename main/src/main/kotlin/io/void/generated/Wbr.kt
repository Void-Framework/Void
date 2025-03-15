package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Wbr(vararg attributes: Attribute): SelfClosingElement("wbr") {
    override val allowedAttributes: List<AttributeNames> = listOf()


    init {
        addAttributes(*attributes)
    }

    fun Element.Wbr(vararg attribute: Attribute): Wbr {
        val Wbr = Wbr(
            attributes = attribute
        )
        children!!.add(Wbr)
        return Wbr
    }
}