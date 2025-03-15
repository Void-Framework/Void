package io.void.generated

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.generated.*
import kotlin.reflect.KClass
import io.void.html.Element
import io.void.html.ElementWithChildren

class Video(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "video") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Track::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.CONTROLS, AttributeNames.AUTOPLAY, AttributeNames.LOOP, AttributeNames.MUTED, AttributeNames.PRELOAD, AttributeNames.POSTER, AttributeNames.WIDTH, AttributeNames.HEIGHT)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

    fun Element.VIDEO(vararg attribute: Attribute, _children: Element.() -> Unit): Video {
        val VIDEO = Video(
            attributes = attribute,
            function = _children
        )
        children!!.add(VIDEO)
        return VIDEO
    }
}