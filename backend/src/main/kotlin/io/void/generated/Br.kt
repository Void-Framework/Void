package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Br(vararg attributes: Attribute) : SelfClosingElement("br") { init { addAttributes(*attributes) } }

@Composable
fun Element.Br(vararg attribute: Attribute): Br {
    val node = Br(attributes = attribute)
    children!!.add(node)
    return node
}
