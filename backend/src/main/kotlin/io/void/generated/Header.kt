package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Header(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "header") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Header(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Header {
    val node = Header(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
