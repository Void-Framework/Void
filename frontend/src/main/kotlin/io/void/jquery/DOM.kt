@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package io.void.jquery

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

@JsName("$")
fun select(element: String): DOMWrapper {
    val nodeList = document.querySelectorAll(element)
    val elements = (0 until nodeList.length).map { nodeList.item(it)!! }
    return DOMWrapper(elements)
}

class DOMWrapper(
    private val elements: List<Element>
) {

    fun text(text: String): DOMWrapper {
        elements.forEach { (it as? HTMLElement)?.innerText = value }
        return this
    }
}

