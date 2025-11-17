package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Form(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "form") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Form(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Form {
    val node = Form(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
