package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Cite(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "cite") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Cite(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Cite {
    val node = Cite(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
