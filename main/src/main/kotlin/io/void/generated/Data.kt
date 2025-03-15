package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Data(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "data") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Data(vararg attribute: Attribute, _children: Element.() -> Unit): Data {
        val Data = Data(
            attributes = attribute,
            function = _children
        )
        children!!.add(Data)
        return Data
    }
