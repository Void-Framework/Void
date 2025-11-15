package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Article(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "article") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Article(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Article {
    val Article =
        Article(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Article)
    return Article
}
