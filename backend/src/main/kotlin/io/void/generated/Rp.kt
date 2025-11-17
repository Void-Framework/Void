package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Rp(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "rp") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Rp(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Rp {
    val node = Rp(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
