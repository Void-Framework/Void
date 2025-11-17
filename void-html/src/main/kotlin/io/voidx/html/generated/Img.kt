package io.voidx.html.generated

import io.voidx.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

@Composable
fun Element.Img(vararg attribute: Attribute) {
    val node = remember {
        object : SelfClosingElement("img") {}
    }
    node.addAttributes(*attribute)
    children!!.add(node)
}
