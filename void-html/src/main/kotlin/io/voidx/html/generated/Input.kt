package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Input(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("input") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
