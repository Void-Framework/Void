package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Colgroup(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "colgroup") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Col::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.COLGROUP(vararg attribute: Attribute, _children: Element.() -> Unit): Colgroup {
        val COLGROUP = Colgroup(
            attributes = attribute,
            function = _children
        )
        children!!.add(COLGROUP)
        return COLGROUP
    }
}