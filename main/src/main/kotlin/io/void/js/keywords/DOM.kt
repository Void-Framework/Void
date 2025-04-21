package io.void.js.keywords

import DirectValue
import JsValue
import io.void.html.Element
import io.void.html.Fractal
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

class DOM(document: Variable<DOM>? = null): BrowserObject {

    override var jsReturn = document?.name ?: "document"

    fun id(id: JsValue<*>): HTMLElement {
        jsReturn += ".getElementById(${id.toJs()})"
        return HTMLElement()
    }
    fun selectAll(identifier: JsValue<*>): JsList<HTMLElement> {
        jsReturn += ".querySelectorAll(${identifier.toJs()})"
        return JsList(listOf(HTMLElement()))
    }
    fun element(element: JsValue<*>): Void {
        jsReturn += ".createElement(${element.toJs()})"
        return Void()
    }
    fun fragment(): Void {
        jsReturn += ".createDocumentFragment()"
        return Void()
    }
    fun elements(amount: JsValue<*>, element: JsValue<*>): BrowserObject {
        jsReturn = "elements(${amount.toJs()}, ${element.toJs()})"
        return this
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

    fun html(element: JsValue<*>): Void {
        jsReturn += ".innerHTML = ${element.toJs()}"
        return Void()
    }
    fun text(newValue: JsValue<*>): Void {
        jsReturn += ".textContent = ${newValue.toJs()}"
        return Void()
    }
    fun clone(children: JsValue<*> = DirectValue(true)): HTMLElement {
        jsReturn += ".cloneNode(${children.toJs()})"
        return HTMLElement()
    }
}

fun JavaScript.id(id: JsValue<*>): HTMLElement {
    val dom = DOM()
    children.add(dom)
    return dom.id(id)
}

fun JavaScript.selectAll(identifier: JsValue<*>): JsList<HTMLElement> {
    val dom = DOM()
    children.add(dom)
    val list = dom.selectAll(identifier)
    children.add(list)
    return list
}

fun JavaScript.elements(amount: JsValue<*>, element: JsValue<*>): BrowserObject {
    val dom = DOM()
    children.add(dom)
    return dom.elements(amount, element)
}
