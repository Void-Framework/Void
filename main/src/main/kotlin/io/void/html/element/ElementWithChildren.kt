package io.void.html.element

import io.void.html.exceptions.ChildNotAllowedException
import kotlin.reflect.KClass

abstract class ElementWithChildren internal constructor(override val name: String): Element(name) {

    abstract val acceptedChildren: MutableList<KClass<out Element>?>

    override fun render(): String {
        var attrs: String = ""
        attributes.entries.forEach { (name, value) ->
            attrs += "${name.name.lowercase()}=\"$value\" "
        }
        /**
         * If the first value in the array is null it means the element can accept any children.
         *
         * Else if you added in a child that is not in the accepted list, the ChildNotAllowedException will be thrown
         *
         * @throws ChildNotAllowedException
         */
        if (acceptedChildren[0] != null) {
            children?.forEach { child ->
                if (!acceptedChildren.contains(child::class)) {
                    throw ChildNotAllowedException(
                        child = child,
                        parent = this
                    )
                }
            }
        }
        val content = children!!.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }
}