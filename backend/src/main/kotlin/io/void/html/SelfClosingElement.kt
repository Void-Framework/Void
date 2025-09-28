package io.void.html

abstract class SelfClosingElement internal constructor(
    override var name: String,
) : Element(name) {
    override val children: MutableList<Element>? = null

    override fun render(): String {
        var attrs = ""
        attributes.forEach { (name, value) ->
            attrs += "${name.lowercase()}=\"$value\" "
        }
        return "<$name $attrs/>"
    }
}
