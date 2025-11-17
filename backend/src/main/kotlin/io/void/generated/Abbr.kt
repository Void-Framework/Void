package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Abbr(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "abbr") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Abbr(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Abbr {
    val node = Abbr(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
