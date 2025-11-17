package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Datalist(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "datalist") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Datalist(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Datalist {
    val node = Datalist(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
