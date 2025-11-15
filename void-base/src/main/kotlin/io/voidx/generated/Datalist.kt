package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Datalist(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "datalist") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Option::class)

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
