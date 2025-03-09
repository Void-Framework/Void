package main.html.element

abstract class ElementWithChildren internal constructor(override val name: String): Element(name) {

    override fun render(): String {
        var attrs: String = ""
        attributes.entries.forEach { (name, value) ->
            attrs += "${name.name.lowercase()}=\"$value\" "
        }
        val content = children!!.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }
}