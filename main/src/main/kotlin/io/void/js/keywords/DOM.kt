package io.void.js.keywords

import io.void.html.Element
import io.void.js.JavaScript

class DOM: Keyword {

    private var jsReturn = "document"

    fun getElementById(id: String): HTMLElement {
        jsReturn += ".getElementById(\"$id\")"
        return HTMLElement()
    }

    fun querySelectorAll(identifier: String): io.void.js.keywords.datastructures.List<HTMLElement> {
        jsReturn += ".querySelectorAll(\"[$identifier]\")"
        return io.void.js.keywords.datastructures.List(listOf(HTMLElement()))
    }

    override fun render(): String {
        return "$jsReturn;"
    }

    inner class HTMLElement {

        fun html(element: Element) {
            jsReturn += ".innerHTML = \"${element.render()}\""
        }

        fun text(newValue: String) {
            jsReturn += ".textContent = \"$newValue\""
        }
    }
}

fun JavaScript.id(id: String): DOM.HTMLElement {
    val dom = DOM()
    children.add(dom)
    return dom.getElementById(id = id)
}

fun JavaScript.selectAll(identifier: String): io.void.js.keywords.datastructures.List<DOM.HTMLElement> {
    val dom = DOM()
    children.add(dom)
    return dom.querySelectorAll(identifier = identifier)
}