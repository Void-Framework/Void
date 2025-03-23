package io.void.html

import io.void.html.exceptions.ChildNotAllowedException
import io.void.html.exceptions.FragmentChildNotAllowedException
import kotlin.reflect.KClass

internal interface HElement

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
         * Else if you added in a child that is not in the accepted list, the ChildNotAllowedException will be thrown.
         * Optionally if the child is a fragment and one of the first set of children doesn't is not in the accepted list, the FragmentChildNotAllowedException will be thrown.
         *
         * @throws ChildNotAllowedException
         */
        if (acceptedChildren[0] != null) {
            children?.forEach { child ->
                if (!isAccepted(child)) {
                    if (child is Fragment) {
                        throw FragmentChildNotAllowedException(
                            parent = this
                        )
                    } else {
                        throw ChildNotAllowedException(
                            child = child,
                            parent = this
                        )
                    }
                }
            }
        }
        val content = children!!.joinToString("") { it.render() }
        return "<$name $attrs>$content</$name>"
    }

    private fun isAccepted(child: Element): Boolean {
        if (!acceptedChildren.contains(child::class) || child is Fragment) {
            if (child is Fragment) {
                if (child.children?.isNotEmpty() == true) {
                    child.children!!.forEach { fChild ->
                        return isAccepted(fChild)
                    }
                } else {
                    return true
                }
            } else {
                return false
            }
        } else {
            return true
        }
        return false
    }
}