package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Template(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "template") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Template(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Template {
    val Template =
        Template(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Template)
    return Template
}
