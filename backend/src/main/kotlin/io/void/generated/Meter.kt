package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Meter(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "meter") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Meter(vararg attribute: Attribute, _children: Element.() -> Unit): Meter {
        val Meter = Meter(
            attributes = attribute,
            function = _children
        )
        children!!.add(Meter)
        return Meter
    }
