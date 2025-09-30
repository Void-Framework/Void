package io.void.html

typealias Attribute = Pair<String, String>

abstract class Element internal constructor(
    open val name: String,
) {
    open val children: MutableList<Element>? = mutableListOf()
    val attributes = mutableListOf<Attribute>()

    abstract fun render(): String

    fun addAttributes(vararg _attributes: Attribute) {
        attributes.addAll(_attributes)
    }
}

fun Element.loop(
    range: IntRange,
    element: Element.(Int) -> Unit,
): Fractal {
    val fragment =
        Fractal {
            for (i in range) {
                element(i)
            }
        }
    return fragment
}

fun Element.kts(block: KtsBuilder.() -> Unit): Element {
    KtsBuilder(this).apply(block)
    return this
}

class KtsBuilder(private val element: Element) {
    fun get(url: String) = element.attributes.add("kts-get" to url)
    fun target(selector: String) = element.attributes.add("kts-target" to selector)
    fun swap(strategy: String) = element.attributes.add("kts-swap" to strategy)
}
