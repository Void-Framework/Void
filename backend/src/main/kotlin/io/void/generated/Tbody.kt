package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Tbody(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "tbody") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Tbody(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Tbody {
    val node = Tbody(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
