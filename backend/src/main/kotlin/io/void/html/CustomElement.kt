package io.void.html

import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

abstract class CustomElement: ElementWithChildren(name = "") {

    override val allowedAttributes: List<AttributeNames> = listOf()
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf()
    abstract val element: Element

    override fun render(): String {
        val content = element.render()
        return content
    }
}