package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Fieldset(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "fieldset") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Fieldset(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Fieldset {
    val Fieldset =
        Fieldset(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Fieldset)
    return Fieldset
}
