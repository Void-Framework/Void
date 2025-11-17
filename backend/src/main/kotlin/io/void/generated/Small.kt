package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Small(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "small") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Small(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Small {
    val node = Small(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
