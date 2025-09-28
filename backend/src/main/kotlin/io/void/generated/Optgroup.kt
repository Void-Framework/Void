package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Optgroup(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "optgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Optgroup(vararg attribute: Attribute, _children: Element.() -> Unit): Optgroup {
        val Optgroup = Optgroup(
            attributes = attribute,
            function = _children
        )
        children!!.add(Optgroup)
        return Optgroup
    }
