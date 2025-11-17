package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Embed(vararg attributes: Attribute) : SelfClosingElement("embed") { init { addAttributes(*attributes) } }

@Composable
fun Element.Embed(vararg attribute: Attribute): Embed {
    val node = Embed(attributes = attribute)
    children!!.add(node)
    return node
}
