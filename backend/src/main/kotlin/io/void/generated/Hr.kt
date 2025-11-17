package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Hr(vararg attributes: Attribute) : SelfClosingElement("hr") { init { addAttributes(*attributes) } }

@Composable
fun Element.Hr(vararg attribute: Attribute): Hr {
    val node = Hr(attributes = attribute)
    children!!.add(node)
    return node
}
