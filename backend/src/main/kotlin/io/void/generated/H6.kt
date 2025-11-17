package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class H6(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "h6") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.H6(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): H6 {
    val node = H6(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
