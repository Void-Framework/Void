package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Embed(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("embed") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
