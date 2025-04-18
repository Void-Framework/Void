package io.void.js.keywords

import io.void.html.Element
import io.void.html.Fractal
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

class DOM(document: Variable<DOM>? = null): BrowserObject {

    override var jsReturn = document?.name ?: "document"

    fun id(id: String): HTMLElement {
        jsReturn += ".getElementById(\"$id\")"
        return HTMLElement()
    }
    fun selectAll(identifier: String): JsList<HTMLElement> {
        jsReturn += ".querySelectorAll(\"$identifier\")"
        return JsList(listOf(HTMLElement()))
    }
    fun element(element: Element): Void {
        jsReturn += ".create${if (element is Fractal) {
            if (element.text.startsWith("<") && element.text.endsWith(">")) {
                "Element"
            } else {
                "TextNode"
            }
        } else {
            "Element"
        }
        }(${element.render()})"
        return Void()
    }
    fun fragment(): Void {
        jsReturn += ".createDocumentFragment()"
        return Void()
    }

    override fun render(): String {
        return jsReturn
    }
}

class HTMLElement: BrowserObject {

    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    fun html(element: Element): Void {
        val elementText = element.render()
        jsReturn += ".innerHTML = '${if (!TemplateString.isTemplateString(elementText)) {
            "\"$elementText\""
        } else {
            "`$elementText`"
        }
        }'"
        return Void()
    }
    fun html(element: Variable<*>): Void {
        jsReturn += ".innerHTML = ${element.name}"
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
    fun text(newValue: Variable<*>): Void {
        jsReturn += ".textContent = ${newValue.name}"
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
    return dom.id(id = id)
}
fun JavaScript.selectAll(identifier: String): JsList<HTMLElement> {
    val dom = DOM()
    children.add(dom)
    val list = dom.selectAll(identifier = identifier)
    children.add(list)
    return list
}
fun JavaScript.elements(amount: Int, element: Element): Void {
    val text = StringBuilder("")
    val attributes = StringBuilder("")
    element.children!!.forEach {
        text.append(it.render())
    }
    element.attributes.forEach { (name, value) ->
        attributes.append("${name.name.lowercase()}: \"$value\",")
    }
    if (element.attributes.isNotEmpty()) {
        attributes.setLength(attributes.length - 1)
    }
    InlineCall("elements($amount, \"${element.name}\", \"$text\", {$attributes})")
    return Void()
}