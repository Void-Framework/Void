package io.void.js.keywords

import io.void.html.Element
import io.void.js.JavaScript

class DOM: Keyword {

    private var jsReturn = "document"

    fun getElementById(id: String): HTMLElement {
        jsReturn += ".getElementById(\"$id\")"
        return HTMLElement()
    }

    override fun render(): String {
        return jsReturn
    }

    inner class HTMLElement {

        fun html(element: Element) {
            jsReturn += ".innerHTML = \"${element.render()}\";"
        }
    }
}

fun JavaScript.id(id: String): DOM.HTMLElement {
    val dom = DOM()
    children.add(dom)
    return dom.getElementById(id)
}