package main.html.element

abstract class ElementWithChildren internal constructor(override val name: String): Element(name) {

    override fun render(): String {
        val attrs = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        val content = children!!.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }
}