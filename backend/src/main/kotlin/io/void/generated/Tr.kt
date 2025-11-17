package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Tr(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "tr") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Tr(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Tr {
    val node = Tr(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
