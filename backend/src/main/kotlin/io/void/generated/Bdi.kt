package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Bdi(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "bdi") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Bdi(vararg attribute: Attribute, _children: Element.() -> Unit): Bdi {
        val Bdi = Bdi(
            attributes = attribute,
            function = _children
        )
        children!!.add(Bdi)
        return Bdi
    }
