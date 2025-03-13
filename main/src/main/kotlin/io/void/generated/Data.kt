package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Data(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "data") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE)
}