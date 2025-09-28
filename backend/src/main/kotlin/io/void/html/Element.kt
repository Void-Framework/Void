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
