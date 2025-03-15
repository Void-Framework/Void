package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Form(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "form") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.ACTION, AttributeNames.METHOD, AttributeNames.ENCTYPE, AttributeNames.NOVALIDATE, AttributeNames.AUTOCOMPLETE, AttributeNames.TARGET, AttributeNames.NAME)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.FORM(vararg attribute: Attribute, _children: Element.() -> Unit): Form {
        val FORM = Form(
            attributes = attribute,
            function = _children
        )
        children!!.add(FORM)
        return FORM
    }
}