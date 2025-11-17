package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Picture(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "picture") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Picture(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Picture {
    val node = Picture(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
