package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Colgroup(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "colgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Colgroup(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Colgroup {
    val node = Colgroup(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
