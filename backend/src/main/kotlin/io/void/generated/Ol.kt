package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Ol(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "ol") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Ol(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Ol {
    val node = Ol(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
