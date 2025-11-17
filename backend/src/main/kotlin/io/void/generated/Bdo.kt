package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Bdo(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "bdo") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Bdo(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Bdo {
    val node = Bdo(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
