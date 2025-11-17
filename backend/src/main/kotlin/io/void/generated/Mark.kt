package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Mark(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "mark") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Mark(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Mark {
    val node = Mark(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
