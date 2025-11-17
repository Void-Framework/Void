package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class H3(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "h3") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.H3(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): H3 {
    val node = H3(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
