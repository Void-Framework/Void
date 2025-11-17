package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Dd(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "dd") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Dd(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Dd {
    val node = Dd(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
