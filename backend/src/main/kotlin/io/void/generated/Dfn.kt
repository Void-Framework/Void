package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Dfn(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "dfn") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Dfn(vararg attribute: Attribute, _children: Element.() -> Unit): Dfn {
        val Dfn = Dfn(
            attributes = attribute,
            function = _children
        )
        children!!.add(Dfn)
        return Dfn
    }
