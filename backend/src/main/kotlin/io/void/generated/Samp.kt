package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Samp(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "samp") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Samp(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Samp {
    val node = Samp(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
