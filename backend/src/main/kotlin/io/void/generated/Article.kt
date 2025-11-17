package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Article(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = "article") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init { this.apply(function); addAttributes(*attributes) }
}
@Composable
fun Element.Article(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): Article {
    val node = Article(attributes = attribute) {
        Fractal(_children)
    }
    children!!.add(node)
    return node
}
