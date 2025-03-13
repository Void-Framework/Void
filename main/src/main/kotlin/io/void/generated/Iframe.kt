package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Iframe(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "iframe") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SRC, AttributeNames.SRCDOC, AttributeNames.WIDTH, AttributeNames.HEIGHT, AttributeNames.NAME, AttributeNames.SANDBOX)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}