package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Sup(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "sup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Sup(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Sup {
    val node = Sup(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
