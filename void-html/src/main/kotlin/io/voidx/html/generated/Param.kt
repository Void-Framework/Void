package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Param(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("param") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
