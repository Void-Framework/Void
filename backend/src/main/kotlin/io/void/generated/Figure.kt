package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Figure(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "figure") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Figure(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Figure {
    val node = Figure(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
