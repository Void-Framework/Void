package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Dt(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "dt") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Dt(vararg attribute: Attribute, _children: Element.() -> Unit): Dt {
        val Dt = Dt(
            attributes = attribute,
            function = _children
        )
        children!!.add(Dt)
        return Dt
    }
