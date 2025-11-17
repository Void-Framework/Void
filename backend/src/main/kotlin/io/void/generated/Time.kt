package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Time(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "time") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Time(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Time {
    val node = Time(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
