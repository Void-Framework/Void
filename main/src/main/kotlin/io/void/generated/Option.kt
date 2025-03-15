package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Option(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "option") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE, AttributeNames.DISABLED, AttributeNames.SELECTED)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.OPTION(vararg attribute: Attribute, _children: Element.() -> Unit): Option {
        val OPTION = Option(
            attributes = attribute,
            function = _children
        )
        children!!.add(OPTION)
        return OPTION
    }
}