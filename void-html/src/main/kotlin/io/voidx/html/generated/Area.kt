package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Area(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("area") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
