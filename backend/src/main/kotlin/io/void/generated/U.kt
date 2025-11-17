package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class U(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "u") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.U(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): U {
    val node = U(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
