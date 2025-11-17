package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Figcaption(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "figcaption") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Figcaption(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Figcaption {
    val node = Figcaption(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
