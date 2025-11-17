package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Input(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("input") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
