package io.voidx.html.generated

import io.voidx.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Menuitem(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("menuitem") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
