package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Video(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "video") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Track::class)
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
