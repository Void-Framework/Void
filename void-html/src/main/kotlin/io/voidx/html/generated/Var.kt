package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Var(vararg attribute: Attribute, child: Element.() -> Unit): ElementWithChildren {
    val node = object : ElementWithChildren(name = "var") {
            override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
        }
    // apply attributes (remembered instance will keep attributes across recompositions)
    node.addAttributes(*attribute)
    // append to parent
    children!!.add(node)
    node.child()
    return node
}
