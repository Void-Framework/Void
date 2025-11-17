package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Dialog(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "dialog") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Dialog(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Dialog {
    val node = Dialog(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
