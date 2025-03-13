package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Audio(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "audio") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Track::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.CONTROLS, AttributeNames.AUTOPLAY, AttributeNames.LOOP, AttributeNames.MUTED, AttributeNames.PRELOAD, AttributeNames.SRC)
}