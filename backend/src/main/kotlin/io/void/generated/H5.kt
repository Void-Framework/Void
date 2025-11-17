package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class H5(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "h5") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.H5(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): H5 {
    val node = H5(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
