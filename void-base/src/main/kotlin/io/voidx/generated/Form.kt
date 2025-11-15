package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Form(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "form") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Form(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Form {
    val Form =
        Form(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Form)
    return Form
}
