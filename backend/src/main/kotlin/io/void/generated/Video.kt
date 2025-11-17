package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Video(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "video") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Video(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Video {
    val node = Video(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
