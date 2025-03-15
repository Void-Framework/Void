package io.void.html

import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Fragment(): ElementWithChildren(name = "") {

    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()
    private lateinit var text: String
    var storedChildren: MutableList<Element> = mutableListOf()

    override fun render(): String {
        return text
    }

    internal constructor(text: String) : this() {
        this.text = text
    }

    internal constructor(children: Element.() -> Unit) : this() {
        val element = this.apply(children)
        this.text = element.render()
        element.children!!.forEach {
            storedChildren.add(it)
        }
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