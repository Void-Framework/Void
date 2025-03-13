package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Table(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "table") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Caption::class, Colgroup::class, Thead::class, Tbody::class, Tfoot::class, Tr::class)
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}