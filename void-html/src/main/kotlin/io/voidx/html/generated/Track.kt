package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Track(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("track") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
