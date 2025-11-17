package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Dl(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "dl") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Dl(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Dl {
    val node = Dl(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
