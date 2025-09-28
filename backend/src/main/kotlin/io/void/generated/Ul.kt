package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Ul(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "ul") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Li::class)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Ul(vararg attribute: Attribute, _children: Element.() -> Unit): Ul {
        val Ul = Ul(
            attributes = attribute,
            function = _children
        )
        children!!.add(Ul)
        return Ul
    }
