package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Hr(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("hr") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
