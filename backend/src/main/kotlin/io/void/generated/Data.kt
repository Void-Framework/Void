package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Data(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "data") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Data(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Data {
    val node = Data(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
