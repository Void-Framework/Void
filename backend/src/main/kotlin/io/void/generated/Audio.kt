package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Audio(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "audio") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Audio(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Audio {
    val node = Audio(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
