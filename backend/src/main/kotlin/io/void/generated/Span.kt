package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Span(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "span") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Span(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Span {
    val node = Span(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
