package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Tfoot(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "tfoot") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Tfoot(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Tfoot {
    val node = Tfoot(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
