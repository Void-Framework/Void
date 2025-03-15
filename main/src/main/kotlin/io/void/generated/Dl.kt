package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Dl(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "dl") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Dt::class, Dd::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.DL(vararg attribute: Attribute, _children: Element.() -> Unit): Dl {
        val DL = Dl(
            attributes = attribute,
            function = _children
        )
        children!!.add(DL)
        return DL
    }
}