package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Audio(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "audio") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(Source::class, Track::class)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Audio(vararg attribute: Attribute, _children: Element.() -> Unit): Audio {
        val Audio = Audio(
            attributes = attribute,
            function = _children
        )
        children!!.add(Audio)
        return Audio
    }
