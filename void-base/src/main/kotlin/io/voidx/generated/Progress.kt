package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Progress(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "progress") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Progress(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Progress {
    val Progress =
        Progress(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Progress)
    return Progress
}
