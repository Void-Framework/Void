package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class S(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "s") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.S(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): S {
    val node = S(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
