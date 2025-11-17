package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Wbr(vararg attributes: Attribute) : SelfClosingElement("wbr") { init { addAttributes(*attributes) } }

@Composable
fun Element.Wbr(vararg attribute: Attribute): Wbr {
    val node = Wbr(attributes = attribute)
    children!!.add(node)
    return node
}
