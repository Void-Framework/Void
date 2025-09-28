package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Th(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "th") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Th(vararg attribute: Attribute, _children: Element.() -> Unit): Th {
        val Th = Th(
            attributes = attribute,
            function = _children
        )
        children!!.add(Th)
        return Th
    }
