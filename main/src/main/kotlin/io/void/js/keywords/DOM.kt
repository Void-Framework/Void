package io.void.js.keywords

import io.void.html.Element
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.event.CustomEvent
import io.void.js.keywords.event.Event
import io.void.js.keywords.event.EventFunction
import io.void.js.keywords.event.exception.FunctionNotVariableException
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

class DOM(document: Variable<DOM>? = null): BrowserObject {

    companion object {
        val element = DOM().HTMLElement()
    }

    override var jsReturn = document?.name ?: "document"

    fun getElementById(id: String): HTMLElement {
        jsReturn += ".getElementById(\"$id\")"
        return HTMLElement()
    }

    fun querySelectorAll(identifier: String): JsList<HTMLElement> {
        jsReturn += ".querySelectorAll(\"$identifier\")"
        return JsList(listOf(HTMLElement()))
    }

    override fun render(): String {
        return jsReturn
    }

    inner class HTMLElement: BrowserObject {

        override var jsReturn: String = ""

        override fun render(): String {
            return jsReturn
        }

        fun html(element: Element): Void {
            jsReturn += ".innerHTML = '${element.render()}'"
            return Void()
        }

        fun text(newValue: String): Void {
            jsReturn += ".textContent = ${if (!TemplateString.isTemplateString(newValue)) {
                "\"$newValue\""
            } else {
                "`$newValue`"
            }
            }"
            return Void()
        }
    }
}

class HTMLElement: BrowserObject {

    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    fun html(element: Element): Void {
        jsReturn += ".innerHTML = '${element.render()}'"
        return Void()
    }
    fun text(newValue: String): Void {
        jsReturn += ".textContent = ${if (!TemplateString.isTemplateString(newValue)) {
            "\"$newValue\""
        } else {
            "`$newValue`"
        }
        }"
        return Void()
    }
    fun clone(children: Boolean = true): HTMLElement {
        jsReturn += ".cloneNode($children)"
        return HTMLElement()
    }
}

fun JavaScript.id(id: String): HTMLElement {
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