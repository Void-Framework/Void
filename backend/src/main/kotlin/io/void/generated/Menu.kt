package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Menu(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "menu") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Menuitem::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.TYPE, AttributeNames.LABEL)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Menu(vararg attribute: Attribute, _children: Element.() -> Unit): Menu {
        val Menu = Menu(
            attributes = attribute,
            function = _children
        )
        children!!.add(Menu)
        return Menu
    }
