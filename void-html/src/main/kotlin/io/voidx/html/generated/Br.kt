package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Br(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("br") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
