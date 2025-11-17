package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Source(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("source") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
