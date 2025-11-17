package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class H4(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "h4") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.H4(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): H4 {
    val node = H4(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
