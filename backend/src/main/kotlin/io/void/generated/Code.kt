package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Code(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "code") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Code(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Code {
    val node = Code(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
