package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Q(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "q") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Q(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Q {
    val node = Q(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
