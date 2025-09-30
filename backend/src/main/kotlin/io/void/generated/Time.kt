package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Time(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "time") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Time(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Time {
    val Time =
        Time(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Time)
    return Time
}
