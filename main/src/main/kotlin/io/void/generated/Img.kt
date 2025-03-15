package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Img(vararg attributes: Attribute): SelfClosingElement("img") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.ALT, AttributeNames.CROSSORIGIN, AttributeNames.HEIGHT, AttributeNames.ISMAP, AttributeNames.LOADING, AttributeNames.REFERERPOLICY, AttributeNames.SIZES, AttributeNames.SRC, AttributeNames.SRCSET, AttributeNames.USEMAP, AttributeNames.WIDTH)


    init {
        addAttributes(*attributes)
    }

    fun Element.IMG(vararg attribute: Attribute): Img {
        val IMG = Img(
            attributes = attribute
        )
        children!!.add(IMG)
        return IMG
    }
}