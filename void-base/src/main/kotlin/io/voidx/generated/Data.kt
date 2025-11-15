package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Data(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "data") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Data(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Data {
    val Data =
        Data(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Data)
    return Data
}
