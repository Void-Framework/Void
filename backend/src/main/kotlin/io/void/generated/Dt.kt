package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Dt(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "dt") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Dt(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Dt {
    val node = Dt(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
