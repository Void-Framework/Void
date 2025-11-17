package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Section(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "section") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Section(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Section {
    val node = Section(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
