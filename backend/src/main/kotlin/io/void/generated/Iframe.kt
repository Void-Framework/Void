package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Iframe(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "iframe") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Iframe(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Iframe {
    val node = Iframe(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
