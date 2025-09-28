package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Kbd(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "kbd") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Kbd(vararg attribute: Attribute, _children: Element.() -> Unit): Kbd {
        val Kbd = Kbd(
            attributes = attribute,
            function = _children
        )
        children!!.add(Kbd)
        return Kbd
    }
