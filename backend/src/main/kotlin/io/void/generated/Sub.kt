package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Sub(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "sub") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Sub(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Sub {
    val node = Sub(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
