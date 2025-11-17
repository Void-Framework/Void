package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Hr(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("hr") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
