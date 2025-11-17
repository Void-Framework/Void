package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Textarea(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "textarea") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Textarea(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Textarea {
    val node = Textarea(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
