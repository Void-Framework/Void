package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Del(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "del") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Del(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Del {
    val node = Del(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
