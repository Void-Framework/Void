package io.voidx.html

import kotlin.reflect.KClass

/**
 * Lightweight container/text node used as a fragment within the HTML DSL.
 *
 * A Fractal can either hold raw [text] or a list of child elements produced via a DSL block.
 */
open class Fractal internal constructor() : ElementWithChildren(name = "") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    private var text: String = ""

    /**
     * Renders either the stored [text] when there are no children, or the
     * concatenated rendering of all child elements.
     */
    override fun render(): String =
        if (children?.isEmpty() == true) {
            text
        } else {
            children?.joinToString("") { it.render() } ?: ""
        }

    /**
     * Creates a text-only fractal node.
     */
    internal constructor(text: String) : this() {
        this.text = text
    }

    /**
     * Creates a container fractal and applies the provided [children] DSL
     * to populate its child elements.
     */
    internal constructor(children: Element.() -> Unit) : this() {
        this.apply(children)
    }
}

/**
 * Appends a text [Fractal] node to the receiver [Element].
 */
fun Element.Fractal(_text: String): Fractal {
    val fractal =
        Fractal(
            text = _text,
        )
    children!!.add(fractal)
    return fractal
}

/**
 * Appends a [Fractal] container node populated by the provided DSL [_children].
 */
fun Element.Fractal(_children: Element.() -> Unit): Fractal {
    val fractal =
        Fractal(
            children = _children,
        )
    children!!.add(fractal)
    return fractal
}

/**
 * Creates a [Fractal] container node populated by the provided DSL [child].
 */
fun fractal(child: Element.() -> Unit): Fractal {
    val fractal =
        Fractal(
            children = child,
        )
    return fractal
}
