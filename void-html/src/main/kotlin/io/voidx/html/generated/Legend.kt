package io.voidx.html.generated

import io.voidx.html.*
import kotlin.reflect.KClass

fun Element.Legend(vararg attribute: Attribute, child: Element.() -> Unit): ElementWithChildren {
    val node = object : ElementWithChildren(name = "legend") {
            override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
        }
    // apply attributes (remembered instance will keep attributes across recompositions)
    node.addAttributes(*attribute)
    // append to parent
    children!!.add(node)
    node.child()
    return node
}
