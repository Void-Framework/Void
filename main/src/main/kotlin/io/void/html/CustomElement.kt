package io.void.html

import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

abstract class CustomElement: ElementWithChildren(name = "") {

    abstract override val allowedAttributes: List<AttributeNames>
    abstract override val acceptedChildren: MutableList<KClass<out Element>?>
    abstract val element: Element

    override fun render(): String {
        val content = element.render()
        return content
    }
}