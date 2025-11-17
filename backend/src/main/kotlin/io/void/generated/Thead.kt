package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Thead(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "thead") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Thead(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Thead {
    val node = Thead(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
