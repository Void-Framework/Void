package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class H1(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "h1") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.H1(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): H1 {
    val node = H1(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
