package main.html.element

abstract class Element(val name: String) {

    val children = mutableListOf<Element>()
    private val attributes = mutableMapOf<String, String>()

    fun render(): String {
        val attrs = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        val content = children.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }
}