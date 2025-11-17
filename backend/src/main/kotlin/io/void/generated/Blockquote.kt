package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Blockquote(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "blockquote") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Blockquote(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Blockquote {
    val node = Blockquote(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
