package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Address(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "address") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Address(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Address {
    val Address =
        Address(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Address)
    return Address
}
