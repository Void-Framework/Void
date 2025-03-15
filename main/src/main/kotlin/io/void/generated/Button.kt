package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Button(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "button") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.TYPE, AttributeNames.NAME, AttributeNames.VALUE, AttributeNames.DISABLED)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.Button(vararg attribute: Attribute, _children: Element.() -> Unit): Button {
        val Button = Button(
            attributes = attribute,
            function = _children
        )
        children!!.add(Button)
        return Button
    }
}