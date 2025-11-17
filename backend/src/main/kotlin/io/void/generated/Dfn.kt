package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Dfn(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "dfn") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Dfn(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Dfn {
    val node = Dfn(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
