package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Button(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "button") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Button(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Button {
    val node = Button(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
