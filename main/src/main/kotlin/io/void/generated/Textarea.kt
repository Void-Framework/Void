package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Textarea(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "textarea") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.NAME, AttributeNames.PLACEHOLDER, AttributeNames.REQUIRED, AttributeNames.DISABLED, AttributeNames.READONLY, AttributeNames.ROWS, AttributeNames.COLS, AttributeNames.MAXLENGTH)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.TEXTAREA(vararg attribute: Attribute, _children: Element.() -> Unit): Textarea {
        val TEXTAREA = Textarea(
            attributes = attribute,
            function = _children
        )
        children!!.add(TEXTAREA)
        return TEXTAREA
    }
}