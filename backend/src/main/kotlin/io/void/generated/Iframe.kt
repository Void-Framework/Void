package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Iframe(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "iframe") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.SRC, AttributeNames.SRCDOC, AttributeNames.WIDTH, AttributeNames.HEIGHT, AttributeNames.NAME, AttributeNames.SANDBOX)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Iframe(vararg attribute: Attribute, _children: Element.() -> Unit): Iframe {
        val Iframe = Iframe(
            attributes = attribute,
            function = _children
        )
        children!!.add(Iframe)
        return Iframe
    }
