package io.voidx.html.generated

import io.voidx.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Del(vararg attribute: Attribute, _children: @Composable Element.() -> Unit) {
    val node = remember {
        object : ElementWithChildren(name = "del") {
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
