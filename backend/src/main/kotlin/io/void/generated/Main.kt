package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Main(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "main") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Main(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Main {
    val node = Main(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
