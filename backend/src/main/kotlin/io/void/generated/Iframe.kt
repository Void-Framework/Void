package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Iframe(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "iframe") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Iframe(vararg attribute: Attribute, _children: Element.() -> Unit): Iframe {
        val Iframe = Iframe(
            attributes = attribute,
            function = _children
        )
        children!!.add(Iframe)
        return Iframe
    }
