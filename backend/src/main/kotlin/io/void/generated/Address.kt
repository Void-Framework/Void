package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Address(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "address") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Address(vararg attribute: Attribute, _children: Element.() -> Unit): Address {
        val Address = Address(
            attributes = attribute,
            function = _children
        )
        children!!.add(Address)
        return Address
    }
