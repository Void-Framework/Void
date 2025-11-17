package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Output(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "output") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Output(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Output {
    val node = Output(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
