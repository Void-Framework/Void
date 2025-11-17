package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Summary(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "summary") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Summary(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Summary {
    val node = Summary(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
