package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Thead(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "thead") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Tr::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Thead(vararg attribute: Attribute, _children: Element.() -> Unit): Thead {
        val Thead = Thead(
            attributes = attribute,
            function = _children
        )
        children!!.add(Thead)
        return Thead
    }
