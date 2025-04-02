package io.void.js.keywords

import io.void.html.Element
import io.void.js.JavaScript

class DOM: Keyword {

    companion object {
        val element = DOM().HTMLElement()
    }

    override var jsReturn = ""

    fun getElementById(id: String): HTMLElement {
        jsReturn = "document.getElementById(\"$id\")"
        return HTMLElement()
    }

    fun querySelectorAll(identifier: String): io.void.js.keywords.datastructures.JsList<HTMLElement> {
        jsReturn = "document.querySelectorAll(\"$identifier\")"
        return io.void.js.keywords.datastructures.JsList(listOf(HTMLElement()))
    }

    override fun render(): String {
        return jsReturn
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

fun JavaScript.selectAll(identifier: String): io.void.js.keywords.datastructures.JsList<DOM.HTMLElement> {
    val dom = DOM()
    children.add(dom)
    return dom.querySelectorAll(identifier = identifier)
}