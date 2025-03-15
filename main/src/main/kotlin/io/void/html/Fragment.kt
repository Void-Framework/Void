package io.void.html

import io.void.html.attributes.AttributeNames
import io.void.html.exceptions.ChildNotAllowedException
import kotlin.reflect.KClass

class Fragment(): ElementWithChildren(name = "") {

    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()
    private lateinit var text: String

    override fun render(): String {
        return text
    }

    constructor(text: String) : this() {
        this.text = text
    }

    constructor(children: Element.() -> Unit) : this() {
        this.text = this.apply(children).render()
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