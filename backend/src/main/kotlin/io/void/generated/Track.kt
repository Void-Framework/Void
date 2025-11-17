package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Track(vararg attributes: Attribute) : SelfClosingElement("track") { init { addAttributes(*attributes) } }

@Composable
fun Element.Track(vararg attribute: Attribute): Track {
    val node = Track(attributes = attribute)
    children!!.add(node)
    return node
}
