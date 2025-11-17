package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Label(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "label") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Label(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Label {
    val node = Label(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
