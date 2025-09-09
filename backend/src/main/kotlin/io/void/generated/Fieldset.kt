package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Fieldset(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "fieldset") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Fieldset(vararg attribute: Attribute, _children: Element.() -> Unit): Fieldset {
        val Fieldset = Fieldset(
            attributes = attribute,
            function = _children
        )
        children!!.add(Fieldset)
        return Fieldset
    }
