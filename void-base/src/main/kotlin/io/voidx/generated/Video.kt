package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import kotlin.reflect.KClass

class Video(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "video") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Track::class)

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.Video(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): Video {
    val Video =
        Video(
            attributes = attribute,
            function = _children,
        )
    children!!.add(Video)
    return Video
}
