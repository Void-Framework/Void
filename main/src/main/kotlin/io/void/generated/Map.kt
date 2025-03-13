package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Map(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "map") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Area::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.NAME)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}