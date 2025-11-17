package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Source(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("source") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
