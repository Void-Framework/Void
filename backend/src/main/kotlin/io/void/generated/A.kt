package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class A(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "a") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.A(vararg attribute: Attribute, _children: Element.() -> Unit): A {
        val A = A(
            attributes = attribute,
            function = _children
        )
        children!!.add(A)
        return A
    }
