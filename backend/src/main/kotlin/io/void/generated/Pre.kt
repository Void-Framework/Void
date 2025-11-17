package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Pre(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "pre") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Pre(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Pre {
    val node = Pre(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
