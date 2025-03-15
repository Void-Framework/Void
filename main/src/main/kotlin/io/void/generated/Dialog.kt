package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Dialog(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "dialog") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.OPEN)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.DIALOG(vararg attribute: Attribute, _children: Element.() -> Unit): Dialog {
        val DIALOG = Dialog(
            attributes = attribute,
            function = _children
        )
        children!!.add(DIALOG)
        return DIALOG
    }
}