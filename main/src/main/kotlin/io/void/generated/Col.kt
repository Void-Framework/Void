package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Col(vararg attributes: Attribute): SelfClosingElement("col") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SPAN)


    init {
        addAttributes(*attributes)
    }

    fun Element.COL(vararg attribute: Attribute): Col {
        val COL = Col(
            attributes = attribute
        )
        children!!.add(COL)
        return COL
    }
}