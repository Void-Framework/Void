package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class I(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "i") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.I(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): I {
    val node = I(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
