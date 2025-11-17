package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Meter(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "meter") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Meter(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Meter {
    val node = Meter(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
