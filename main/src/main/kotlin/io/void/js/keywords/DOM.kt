package io.void.js.keywords

import io.void.html.Element
import io.void.html.Fractal
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable
import java.lang.UnsupportedOperationException

class DOM(document: Variable<DOM>? = null): BrowserObject {

    override var jsReturn = document?.name ?: "document"

    fun id(id: JsValue<String>): HTMLElement {
        jsReturn += ".getElementById(${id.toJs()})"
        return HTMLElement()
    }
    fun selectAll(identifier: JsValue<String>): JsList<HTMLElement> {
        jsReturn += ".querySelectorAll(${identifier.toJs()})"
        return JsList(HTMLElement().asJsValue())
    }
    fun element(element: JsValue<Element>): HTMLElement {
        jsReturn += ".createElement(${element.toJs()})"
        return HTMLElement()
    }
    fun fragment(): BrowserObject {
        jsReturn += ".createDocumentFragment()"
        return this
    }
    fun elements(amount: JsValue<Int>, element: JsValue<Element>): BrowserObject {
        val elementValue = when (element) {
            is DirectValue<Element> -> element.value
            is VariableValue<Element> -> element.variable.value!!
            else -> throw UnsupportedOperationException("Cannot derive element from js function")
        }
        val attributes = elementValue.attributes.toList().joinToString("") { (name, value) ->
            "$name: \"$value\","
        }
        if (elementValue.attributes.isNotEmpty()) attributes.replaceAfterLast(",", "")
        jsReturn = "elements(${amount.toJs()}, \"${elementValue.name}\", \"${elementValue.children!!.joinToString("") { it.render() }}\", {$attributes})"
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

    fun html(element: JsValue<*>): HTMLElement {
        jsReturn += ".innerHTML = ${element.toJs()}"
        return this
    }
    fun text(newValue: JsValue<String>): HTMLElement {
        jsReturn += ".textContent = ${newValue.toJs()}"
        return this
    }
    fun clone(children: JsValue<Boolean> = DirectValue(true)): HTMLElement {
        jsReturn += ".cloneNode(${children.toJs()})"
        return HTMLElement()
    }
}

fun JavaScript.id(id: JsValue<String>): HTMLElement {
    val dom = DOM()
    children.add(dom)
    return dom.id(id)
}

fun JavaScript.selectAll(identifier: JsValue<String>): JsList<HTMLElement> {
    val dom = DOM()
    children.add(dom)
    val list = dom.selectAll(identifier)
    children.add(list)
    return list
}

fun JavaScript.elements(amount: JsValue<Int>, element: JsValue<Element>): BrowserObject {
    val dom = DOM()
    children.add(dom)
    return dom.elements(amount, element)
}
