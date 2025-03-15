package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Datalist(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "datalist") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.DATALIST(vararg attribute: Attribute, _children: Element.() -> Unit): Datalist {
        val DATALIST = Datalist(
            attributes = attribute,
            function = _children
        )
        children!!.add(DATALIST)
        return DATALIST
    }
}