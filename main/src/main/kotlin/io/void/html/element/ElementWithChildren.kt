package io.void.html.element

import io.void.html.exceptions.ChildNotAllowedException

abstract class ElementWithChildren internal constructor(override val name: String): Element(name) {

    abstract val acceptedChildren: MutableList<Element?>

    override fun render(): String {
        var attrs: String = ""
        attributes.entries.forEach { (name, value) ->
            attrs += "${name.name.lowercase()}=\"$value\" "
        }
        children?.forEach { child ->
            if (!acceptedChildren.contains(child)) {
                throw ChildNotAllowedException(
                    child = child,
                    parent = this
                    )
            }
        }
        val content = children!!.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }
}