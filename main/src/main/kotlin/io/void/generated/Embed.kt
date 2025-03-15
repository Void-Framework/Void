package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Embed(vararg attributes: Attribute): SelfClosingElement("embed") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SRC, AttributeNames.TYPE, AttributeNames.WIDTH, AttributeNames.HEIGHT)


    init {
        addAttributes(*attributes)
    }

    fun Element.EMBED(vararg attribute: Attribute): Embed {
        val EMBED = Embed(
            attributes = attribute
        )
        children!!.add(EMBED)
        return EMBED
    }
}