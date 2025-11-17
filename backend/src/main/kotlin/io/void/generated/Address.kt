package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Address(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "address") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Address(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Address {
    val node = Address(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
