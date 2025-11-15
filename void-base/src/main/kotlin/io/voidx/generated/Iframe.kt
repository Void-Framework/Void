package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Iframe(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "iframe") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Iframe(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Iframe {
    val Iframe =
        Iframe(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Iframe)
    return Iframe
}
