package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Bdi(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "bdi") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Bdi(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Bdi {
    val node = Bdi(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
