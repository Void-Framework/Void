package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Param(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("param") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
