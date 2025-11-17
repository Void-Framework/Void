package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Img(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("img") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
