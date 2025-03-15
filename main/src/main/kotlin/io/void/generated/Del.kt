package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Del(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "del") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.CITE, AttributeNames.DATETIME)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Del(vararg attribute: Attribute, _children: Element.() -> Unit): Del {
        val Del = Del(
            attributes = attribute,
            function = _children
        )
        children!!.add(Del)
        return Del
    }
