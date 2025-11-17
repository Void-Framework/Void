package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Wbr(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("wbr") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
