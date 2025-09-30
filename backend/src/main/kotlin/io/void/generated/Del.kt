package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Del(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "del") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Del(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Del {
    val Del =
        Del(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Del)
    return Del
}
