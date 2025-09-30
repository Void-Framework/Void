package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.SelfClosingElement

class Track(
    vararg attributes: Attribute,
) : SelfClosingElement("track") {
    init {
        addAttributes(*attributes)
    }
}

fun Element.Track(vararg attribute: Attribute): Track {
    val Track =
        Track(
            attributes = attribute,
        )
    children!!.add(Track)
    return Track
}
