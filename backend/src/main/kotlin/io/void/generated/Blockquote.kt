package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Blockquote(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "blockquote") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.CITE)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Blockquote(vararg attribute: Attribute, _children: Element.() -> Unit): Blockquote {
        val Blockquote = Blockquote(
            attributes = attribute,
            function = _children
        )
        children!!.add(Blockquote)
        return Blockquote
    }
