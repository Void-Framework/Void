package io.void.js.keywords

import io.void.html.Element
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

class DOM(document: Variable<DOM>? = null): Keyword {

    companion object {
        val element = DOM().HTMLElement()
    }

    override var jsReturn = document?.name ?: "document"

    fun getElementById(id: String): HTMLElement {
        jsReturn = ".getElementById(\"$id\")"
        return HTMLElement()
    }

    fun querySelectorAll(identifier: String): JsList<HTMLElement> {
        jsReturn = ".querySelectorAll(\"$identifier\")"
        return JsList(listOf(HTMLElement()))
    }

    override fun render(): String {
        return jsReturn
    }

    inner class HTMLElement {

        fun html(element: Element) {
            jsReturn += ".innerHTML = '${element.render()}'"
        }

        fun text(newValue: String) {
            jsReturn += ".textContent = ${if (!TemplateString.isTemplateString(newValue)) {
                "\"$newValue\""
            } else {
                "`$newValue`"
            }
            }"
        }
    }
}

fun JavaScript.id(id: String): DOM.HTMLElement {
    val dom = DOM()
    children.add(dom)
    return dom.getElementById(id = id)
}

fun JavaScript.selectAll(identifier: String): JsList<DOM.HTMLElement> {
    val dom = DOM()
    children.add(dom)
    val list = dom.querySelectorAll(identifier = identifier)
    children.add(list)
    return list
}