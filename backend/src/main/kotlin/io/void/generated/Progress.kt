package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Progress(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "progress") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Progress(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Progress {
    val node = Progress(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
