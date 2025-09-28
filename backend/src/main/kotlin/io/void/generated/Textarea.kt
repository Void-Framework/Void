package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Textarea(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "textarea") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Textarea(vararg attribute: Attribute, _children: Element.() -> Unit): Textarea {
        val Textarea = Textarea(
            attributes = attribute,
            function = _children
        )
        children!!.add(Textarea)
        return Textarea
    }
