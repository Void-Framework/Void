package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Form(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "form") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.ACTION, AttributeNames.METHOD, AttributeNames.ENCTYPE, AttributeNames.NOVALIDATE, AttributeNames.AUTOCOMPLETE, AttributeNames.TARGET, AttributeNames.NAME)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Form(vararg attribute: Attribute, _children: Element.() -> Unit): Form {
        val Form = Form(
            attributes = attribute,
            function = _children
        )
        children!!.add(Form)
        return Form
    }
