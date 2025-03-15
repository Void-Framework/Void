package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Tbody(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "tbody") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Tr::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.Tbody(vararg attribute: Attribute, _children: Element.() -> Unit): Tbody {
        val Tbody = Tbody(
            attributes = attribute,
            function = _children
        )
        children!!.add(Tbody)
        return Tbody
    }
}