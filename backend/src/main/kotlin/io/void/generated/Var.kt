package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Var(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "var") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Var(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Var {
    val node = Var(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
