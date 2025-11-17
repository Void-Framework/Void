package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Menuitem(vararg attributes: Attribute) : SelfClosingElement("menuitem") { init { addAttributes(*attributes) } }

@Composable
fun Element.Menuitem(vararg attribute: Attribute): Menuitem {
    val node = Menuitem(attributes = attribute)
    children!!.add(node)
    return node
}
