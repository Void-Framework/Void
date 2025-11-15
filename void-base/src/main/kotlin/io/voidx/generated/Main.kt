package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Main(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "main") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Main(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Main {
    val Main =
        Main(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Main)
    return Main
}
