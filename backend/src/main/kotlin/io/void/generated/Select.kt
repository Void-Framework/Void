package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Select(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "select") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Select(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Select {
    val node = Select(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
