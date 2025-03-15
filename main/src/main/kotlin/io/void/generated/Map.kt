package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Map(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "map") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Area::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.NAME)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Map(vararg attribute: Attribute, _children: Element.() -> Unit): Map {
        val Map = Map(
            attributes = attribute,
            function = _children
        )
        children!!.add(Map)
        return Map
    }
