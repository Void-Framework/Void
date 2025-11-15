package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Bdo(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "bdo") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Bdo(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Bdo {
    val Bdo =
        Bdo(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Bdo)
    return Bdo
}
