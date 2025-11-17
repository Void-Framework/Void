package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Param(vararg attributes: Attribute) : SelfClosingElement("param") { init { addAttributes(*attributes) } }

@Composable
fun Element.Param(vararg attribute: Attribute): Param {
    val node = Param(attributes = attribute)
    children!!.add(node)
    return node
}
