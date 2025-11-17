package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Td(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "td") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Td(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Td {
    val node = Td(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
