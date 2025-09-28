package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Aside(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "aside") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Aside(vararg attribute: Attribute, _children: Element.() -> Unit): Aside {
        val Aside = Aside(
            attributes = attribute,
            function = _children
        )
        children!!.add(Aside)
        return Aside
    }
