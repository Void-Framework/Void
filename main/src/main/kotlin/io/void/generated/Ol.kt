package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Ol(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "ol") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Li::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.REVERSED, AttributeNames.START, AttributeNames.TYPE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Ol(vararg attribute: Attribute, _children: Element.() -> Unit): Ol {
        val Ol = Ol(
            attributes = attribute,
            function = _children
        )
        children!!.add(Ol)
        return Ol
    }
