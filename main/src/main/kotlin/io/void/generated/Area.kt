package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.SelfClosingElement
import io.void.html.Element

class Area(vararg attributes: Attribute): SelfClosingElement("area") {
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.ALT, AttributeNames.COORDS, AttributeNames.SHAPE, AttributeNames.HREF, AttributeNames.TARGET)


    init {
        addAttributes(*attributes)
    }

    fun Element.Area(vararg attribute: Attribute): Area {
        val Area = Area(
            attributes = attribute
        )
        children!!.add(Area)
        return Area
    }
}