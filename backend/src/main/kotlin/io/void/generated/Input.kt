package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Input(vararg attributes: Attribute) : SelfClosingElement("input") { init { addAttributes(*attributes) } }

@Composable
fun Element.Input(vararg attribute: Attribute): Input {
    val node = Input(attributes = attribute)
    children!!.add(node)
    return node
}
