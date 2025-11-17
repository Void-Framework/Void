package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class A(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "a") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.A(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): A {
    val node = A(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
