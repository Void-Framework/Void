package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Ul(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "ul") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Ul(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Ul {
    val node = Ul(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
