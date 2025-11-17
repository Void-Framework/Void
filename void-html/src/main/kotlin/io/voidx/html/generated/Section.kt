package io.voidx.html.generated

import io.voidx.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Section(vararg attribute: Attribute, _children: @Composable Element.() -> Unit) {
    val node = remember {
        object : ElementWithChildren(name = "section") {
            // Accept-any-children for now (see notes)
            override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
        }
    }
    // apply attributes (remembered instance will keep attributes across recompositions)
    node.addAttributes(*attribute)
    // append to parent
    children!!.add(node)
    with(node) { _children() }
}
