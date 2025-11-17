package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class P(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "p") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.P(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): P {
    val node = P(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
