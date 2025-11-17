package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Rt(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "rt") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Rt(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Rt {
    val node = Rt(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
