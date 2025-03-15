package io.void.generated

import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class Video(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "video") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Track::class)
    override val allowedAttributes: List<AttributeNames> = listOf(AttributeNames.CONTROLS, AttributeNames.AUTOPLAY, AttributeNames.LOOP, AttributeNames.MUTED, AttributeNames.PRELOAD, AttributeNames.POSTER, AttributeNames.WIDTH, AttributeNames.HEIGHT)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Video(vararg attribute: Attribute, _children: Element.() -> Unit): Video {
        val Video = Video(
            attributes = attribute,
            function = _children
        )
        children!!.add(Video)
        return Video
    }
