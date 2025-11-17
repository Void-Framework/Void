package io.void.generated

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass

class Img(vararg attributes: Attribute) : SelfClosingElement("img") { init { addAttributes(*attributes) } }

@Composable
fun Element.Img(vararg attribute: Attribute): Img {
    val node = Img(attributes = attribute)
    children!!.add(node)
    return node
}
