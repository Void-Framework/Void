package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class B(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "b") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.B(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): B {
    val node = B(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
