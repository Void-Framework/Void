package io.void.js.keywords

import io.void.html.Element
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.JsObject
import io.void.js.keywords.variable.Variable
import java.lang.UnsupportedOperationException

class DOM(document: Variable<DOM>? = null): BrowserObject {

    override var jsReturn = document?.name ?: "document"

    fun id(id: JsValue<String>): HTMLElement {
        jsReturn += ".getElementById(${id.toJs()})"
        return HTMLElement()
    }
    fun cssClass(cssClass: JsValue<String>): HTMLElement {
        jsReturn += ".getElementByClassName($cssClass)"
        return HTMLElement()
    }
    fun name(name: JsValue<String>): JsList<HTMLElement> {
        jsReturn += ".getElementsByName($name)"
        return JsList(HTMLElement().asJsValue())
    }
    fun tag(tag: JsValue<String>): JsList<HTMLElement> {
        jsReturn += ".getElementsByTagName($tag)"
        return JsList(HTMLElement().asJsValue())
    }
    fun selectAll(identifier: JsValue<String>): JsList<HTMLElement> {
        jsReturn += ".querySelectorAll(${identifier.toJs()})"
        return JsList(HTMLElement().asJsValue())
    }
    fun select(identifier: JsValue<String>): HTMLElement {
        jsReturn += ".querySelector($identifier)"
        return HTMLElement()
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
    fun body(): HTMLElement {
        jsReturn += ".body"
        return HTMLElement()
    }
    fun attribute(names: AttributeNames): JsAttribute {
        jsReturn += ".createAttribute(\"${names.name.lowercase()}\")"
        return JsAttribute()
    }
    fun attribute(name: JsValue<String>): JsAttribute {
        jsReturn += ".createAttribute($name)"
        return JsAttribute()
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
    fun attribute(attribute: AttributeNames): JsValue<String> {
        jsReturn += ".getAttribute(\"${attribute.name.lowercase()}\")"
        return "".asJsValue()
    }
    fun attribute(attribute: Attribute): Reference<HTMLElement>  {
        jsReturn += ".setAttribute(\"${attribute.name.name.lowercase()}\", \"${attribute.value}\")"
        return this.refer()
    }
    fun attribute(attribute: JsValue<String>): JsValue<String>  {
        jsReturn += ".getAttribute($attribute)"
        return "".asJsValue()
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
    fun put(element: JsValue<HTMLElement>, type: PutType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase()}($element)"
        return this.refer()
    }
    fun put(element: JsValue<String>, type: PutType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase()}($element)"
        return this.refer()
    }
    fun put(element: JsList<HTMLElement>, type: PutType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase()}(${element.render()})"
        return this.refer()
    }
    fun put(element: JsList<String>, type: PutType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase()}(${element.render()})"
        return this.refer()
    }
    fun replace(element: JsValue<HTMLElement>, type: ReplaceType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase().uppercase()}($element)"
        return this.refer()
    }
    fun replace(element: JsValue<String>, type: ReplaceType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase().uppercase()}($element)"
        return this.refer()
    }
    fun replace(element: JsList<HTMLElement>, type: ReplaceType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase().uppercase()}(${element.render()})"
        return this.refer()
    }
    fun replace(element: JsList<String>, type: ReplaceType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase().uppercase()}(${element.render()})"
        return this.refer()
    }
}

class JsAttribute: Keyword {
    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }

    fun value(): JsValue<String> {
        jsReturn += ".value"
        return "".asJsValue()
    }
    fun name(): JsValue<String> {
        jsReturn += ".name"
        return "".asJsValue()
    }
    fun value(value: JsValue<String>): JsValue<String> {
        jsReturn += ".value = $value"
        return "".asJsValue()
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
