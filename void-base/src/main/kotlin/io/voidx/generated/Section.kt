package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Section(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "section") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Section(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Section {
    val Section =
        Section(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Section)
    return Section
}
