package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Meter(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "meter") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE, AttributeNames.MIN, AttributeNames.MAX, AttributeNames.LOW, AttributeNames.HIGH, AttributeNames.OPTIMUM)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.METER(vararg attribute: Attribute, _children: Element.() -> Unit): Meter {
        val METER = Meter(
            attributes = attribute,
            function = _children
        )
        children!!.add(METER)
        return METER
    }
}