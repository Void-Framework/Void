package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Template(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "template") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Template(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Template {
    val node = Template(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
