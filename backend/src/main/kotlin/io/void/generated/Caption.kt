package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Caption(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "caption") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Caption(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Caption {
    val node = Caption(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
