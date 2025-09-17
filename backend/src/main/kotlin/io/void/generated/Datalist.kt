package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Datalist(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "datalist") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Datalist(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Datalist {
    val Datalist =
        Datalist(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Datalist)
    return Datalist
}
