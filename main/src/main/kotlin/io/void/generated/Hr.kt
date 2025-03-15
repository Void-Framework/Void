package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Hr(vararg attributes: Attribute): SelfClosingElement("hr") {
    override val allowedAttributes: List<AttributeNames> = listOf()


    init {
        addAttributes(*attributes)
    }

    fun Element.HR(vararg attribute: Attribute): Hr {
        val HR = Hr(
            attributes = attribute
        )
        children!!.add(HR)
        return HR
    }
}