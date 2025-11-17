package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Optgroup(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "optgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Optgroup(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Optgroup {
    val node = Optgroup(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
