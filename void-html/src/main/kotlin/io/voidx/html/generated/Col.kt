package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Col(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("col") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
