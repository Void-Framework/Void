package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Param(vararg attributes: Attribute): SelfClosingElement("param") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.NAME, AttributeNames.VALUE)


    init {
        addAttributes(*attributes)
    }

    fun Element.Param(vararg attribute: Attribute): Param {
        val Param = Param(
            attributes = attribute
        )
        children!!.add(Param)
        return Param
    }
}