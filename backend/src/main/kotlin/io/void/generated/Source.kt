package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Source(vararg attributes: Attribute) : SelfClosingElement("source") { init { addAttributes(*attributes) } }

@Composable
fun Element.Source(vararg attribute: Attribute): Source {
    val node = Source(attributes = attribute)
    children!!.add(node)
    return node
}
