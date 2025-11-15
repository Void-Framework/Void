package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

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
