package io.void.js.keywords

import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.JsObject
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
    fun elements(amount: JsValue<Int>, element: JsValue<HTMLElement>, elementInsides: JsValue<String>, attribute: JsValue<JsObject>): BrowserObject {
        jsReturn = "elements(${amount.toJs()}, ${element}, ${elementInsides}, $attribute)"
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

    fun html(element: JsValue<*>): Reference<HTMLElement> {
        jsReturn += ".innerHTML = ${element.toJs()}"
        return this.refer()
    }
    fun text(newValue: JsValue<String>): Reference<HTMLElement> {
        jsReturn += ".textContent = ${newValue.toJs()}"
        return this.refer()
    }
    fun clone(children: JsValue<Boolean> = DirectValue(true)): HTMLElement {
        jsReturn += ".cloneNode(${children.toJs()})"
        return HTMLElement()
    }
    fun attribute(attribute: AttributeNames): HTMLElement {
        jsReturn += ".getAttribute(\"${attribute.name.lowercase()}\")"
        return this
    }
    fun attribute(attribute: Attribute): Reference<HTMLElement>  {
        jsReturn += ".setAttribute(\"${attribute.name.name.lowercase()}\", \"${attribute.value}\")"
        return this.refer()
    }
    fun attribute(attribute: JsValue<String>): HTMLElement  {
        jsReturn += ".getAttribute($attribute)"
        return this
    }
    fun attribute(attributeName: JsValue<String>, attributeValue: JsValue<String>): Reference<HTMLElement>  {
        jsReturn += ".setAttribute($attributeName, $attributeValue)"
        return this.refer()
    }
    fun attributes(element: JsValue<HTMLElement>, attrbitues: JsValue<JsObject>): Reference<HTMLElement> {
        jsReturn = "attributes($element, $attrbitues)"
        return this.refer()
    }
    fun parent(onlyElements: Boolean): HTMLElement {
        jsReturn += ".parent${if (onlyElements) "Element" else "Node"}"
        return HTMLElement()
    }
    fun children(): JsList<HTMLElement> {
        jsReturn += ".children"
        return JsList(HTMLElement().asJsValue())
    }
    fun sibling(next: Boolean): HTMLElement {
        jsReturn += ".${if (next) "next" else "previous"}Sibling"
        return HTMLElement()
    }
    fun closest(id: JsValue<String>): HTMLElement {
        jsReturn += ".closest($id)"
        return HTMLElement()
    }
    fun matches(id: JsValue<String>): JsValue<Boolean> {
        jsReturn += ".matches($id)"
        return true.asJsValue()
    }
    fun getRect(): JsObject {
        jsReturn += ".getBoundingClientRect()"
        return JsObject(mapOf())
    }
    fun getOffset(): JsObject {
        jsReturn += ".offset()"
        return JsObject(mapOf())
    }
    fun getDimensions(): JsObject {
        jsReturn += ".getBoundingClientRect()"
        return JsObject(mapOf())
    }
    fun style(id: JsValue<String>, value: JsValue<String>): Reference<HTMLElement> {
        jsReturn += ".style.$id = $value"
        return this.refer()
    }
    fun style(id: JsValue<String>): JsValue<String> {
        jsReturn += ".style.$id"
        return "".asJsValue()
    }
    fun selectAll(identifier: JsValue<String>): JsList<HTMLElement> {
        jsReturn += ".querySelectorAll(${identifier.toJs()})"
        return JsList(HTMLElement().asJsValue())
    }
    fun before(child: JsValue<HTMLElement>, element: JsValue<HTMLElement>): HTMLElement {
        jsReturn += ".insertBefore($child, $element)"
        return HTMLElement()
    }
    fun replace(child: JsValue<HTMLElement>, element: JsValue<HTMLElement>): HTMLElement {
        jsReturn += ".replaceChild($child, $element)"
        return HTMLElement()
    }
    fun remove() {
        jsReturn += ".remove()"
    }
    fun remove(element: JsValue<HTMLElement>): HTMLElement {
        jsReturn += ".removeChild($element)"
        return HTMLElement()
    }
    fun add(element: JsValue<HTMLElement>): HTMLElement {
        jsReturn += ".appendChild($element)"
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
