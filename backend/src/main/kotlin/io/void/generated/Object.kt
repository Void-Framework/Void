package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Object(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "object") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.DATA, AttributeNames.TYPE, AttributeNames.WIDTH, AttributeNames.HEIGHT)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Object(vararg attribute: Attribute, _children: Element.() -> Unit): Object {
        val Object = Object(
            attributes = attribute,
            function = _children
        )
        children!!.add(Object)
        return Object
    }
