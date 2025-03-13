package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement

class Img(vararg attribute: Attribute): SelfClosingElement("img") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.ALT, AttributeNames.CROSSORIGIN, AttributeNames.HEIGHT, AttributeNames.ISMAP, AttributeNames.LOADING, AttributeNames.REFERERPOLICY, AttributeNames.SIZES, AttributeNames.SRC, AttributeNames.SRCSET, AttributeNames.USEMAP, AttributeNames.WIDTH)


    init {
        addAttributes(*attribute)
    }

}