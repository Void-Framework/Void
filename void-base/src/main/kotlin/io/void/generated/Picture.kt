package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Picture(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "picture") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Img::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Picture(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Picture {
    val Picture =
        Picture(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Picture)
    return Picture
}
