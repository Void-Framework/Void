package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
