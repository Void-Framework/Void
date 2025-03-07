package main.html.element

import main.html.attributes.Attribute
import main.html.attributes.exception.UnsupportedTypeException
import main.html.element.content.*
import main.html.element.content.formatting.Br
import main.html.element.content.formatting.Hr

abstract class ElementWithChildren internal constructor(override val name: String): Element(name) {

    override fun render(): String {
        val attrs = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        val content = children!!.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }
}