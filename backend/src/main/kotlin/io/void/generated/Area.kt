package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Area(vararg attributes: Attribute) : SelfClosingElement("area") { init { addAttributes(*attributes) } }

@Composable
fun Element.Area(vararg attribute: Attribute): Area {
    val node = Area(attributes = attribute)
    children!!.add(node)
    return node
}
