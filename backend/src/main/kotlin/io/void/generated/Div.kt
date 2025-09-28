package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Div(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "div") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Div(vararg attribute: Attribute, _children: Element.() -> Unit): Div {
        val Div = Div(
            attributes = attribute,
            function = _children
        )
        children!!.add(Div)
        return Div
    }
