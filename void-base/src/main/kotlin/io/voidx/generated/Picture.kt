package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
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
