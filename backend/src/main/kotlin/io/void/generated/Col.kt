package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Col(vararg attributes: Attribute) : SelfClosingElement("col") { init { addAttributes(*attributes) } }

@Composable
fun Element.Col(vararg attribute: Attribute): Col {
    val node = Col(attributes = attribute)
    children!!.add(node)
    return node
}
