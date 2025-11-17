package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Ruby(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "ruby") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Ruby(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Ruby {
    val node = Ruby(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
