package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Br(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("br") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
