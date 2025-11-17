package io.voidx.html

/**
 * Base class for HTML elements that cannot contain children (e.g., <img>, <br>, <input>).
 *
 * Renders as a self-closing tag. Any attempt to add children is prevented by exposing
 * [children] as null. Attributes are rendered in lowercase name form.
 */
abstract class SelfClosingElement internal constructor(
    override var name: String,
) : Element(name) {
    override val children: MutableList<Element>? = null

    /**
     * Renders a self-closing tag with serialized attributes, e.g. <img src="..." />.
     */
    override fun render(): String {
        var attrs = ""
        attributes.forEach { (name, value) ->
            attrs += "${name.lowercase()}=\"$value\" "
        }
        return "<$name $attrs/>"
    }
}
