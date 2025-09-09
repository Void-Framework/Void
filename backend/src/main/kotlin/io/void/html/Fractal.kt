package io.void.html

import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

open class Fractal internal constructor(): ElementWithChildren(name = "") {

    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    override val allowedAttributes: List<AttributeNames> = listOf()
    var storedChildren: MutableList<Element> = mutableListOf()
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

fun Element.Fractal(_text: String): Fractal {
    val fractal = Fractal(
        text = _text
    )
    children!!.add(fractal)
    return fractal
}

fun Element.Fractal(_children: Element.() -> Unit): Fractal {
    val fractal = Fractal(
        children = _children
    )
    children!!.add(fractal)
    return fractal
}