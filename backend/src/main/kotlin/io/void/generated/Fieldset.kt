package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Fieldset(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "fieldset") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Fieldset(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Fieldset {
    val node = Fieldset(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
