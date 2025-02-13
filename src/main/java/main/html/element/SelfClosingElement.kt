package main.html.element

abstract class SelfClosingElement(override var name: String): Element(name) {

    override val children: MutableList<Element>? = null

    override fun render(): String {
        val attrs = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        return "<$name $attrs/>"
    }
}