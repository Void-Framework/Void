package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Output(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "output") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.NAME)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.Output(vararg attribute: Attribute, _children: Element.() -> Unit): Output {
        val Output = Output(
            attributes = attribute,
            function = _children
        )
        children!!.add(Output)
        return Output
    }
}