package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Menuitem(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("menuitem") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
