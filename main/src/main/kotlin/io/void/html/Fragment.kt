package io.void.html

import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Fragment internal constructor(): ElementWithChildren(name = "") {

    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()
    private var text: String = ""

    override fun render(): String {
        return if (children?.isEmpty() == true) {
            text
        } else {
            children?.joinToString("") { it.render() } ?: ""
        }
    }

    internal constructor(text: String) : this() {
        this.text = text
    }

    internal constructor(children: Element.() -> Unit) : this() {
        this.apply(children)
    }
}

fun Element.Fragment(_text: String): Fragment {
    val fragment = Fragment(
        text = _text
    )
    children!!.add(fragment)
    return fragment
}

fun Element.Fragment(_children: Element.() -> Unit): Fragment {
    val fragment = Fragment(
        children = _children
    )
    children!!.add(fragment)
    return fragment
}