package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Source(vararg attributes: Attribute): SelfClosingElement("source") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SRC, AttributeNames.TYPE, AttributeNames.MEDIA)


    init {
        addAttributes(*attributes)
    }

    fun Element.SOURCE(vararg attribute: Attribute): Source {
        val SOURCE = Source(
            attributes = attribute
        )
        children!!.add(SOURCE)
        return SOURCE
    }
}