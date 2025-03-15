package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Progress(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "progress") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.VALUE, AttributeNames.MAX)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.Progress(vararg attribute: Attribute, _children: Element.() -> Unit): Progress {
        val Progress = Progress(
            attributes = attribute,
            function = _children
        )
        children!!.add(Progress)
        return Progress
    }
}