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

    fun id(id: JsValue<String>, call: (HTMLElement) -> Unit): Reference<DOM> {
        jsReturn += ".getElementById(${id.toJs()})"
        return applyMethods(call, HTMLElement(), this)
    }
    fun cssClass(cssClass: JsValue<String>, call: (HTMLElement) -> Unit): Reference<DOM> {
        jsReturn += ".getElementsByClassName($cssClass)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun name(name: JsValue<String>, call: (JsList<HTMLElement>) -> Unit): Reference<DOM> {
        jsReturn += ".getElementsByName($name)"
        return applyMethods(call, JsList(HTMLElement().asJsValue()), this)
    }
    fun tag(tag: JsValue<String>, call: (JsList<HTMLElement>) -> Unit): Reference<DOM> {
        jsReturn += ".getElementsByTagName($tag)"
        return applyMethods(call, JsList(HTMLElement().asJsValue()), this)
    }
    fun selectAll(identifier: JsValue<String>, call: (JsList<HTMLElement>) -> Unit): Reference<DOM> {
        jsReturn += ".querySelectorAll(${identifier.toJs()})"
        return applyMethods(call, JsList(HTMLElement().asJsValue()), this)
    }
    fun select(identifier: JsValue<String>, call: (HTMLElement) -> Unit): Reference<DOM> {
        jsReturn += ".querySelector($identifier)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun element(element: JsValue<Element>, call: (HTMLElement) -> Unit): Reference<DOM> {
        jsReturn += ".createElement(${element.toJs()})"
        return applyMethods(call, HTMLElement(), this)
    }
    fun fragment(call: (BrowserObject) -> Unit): Reference<DOM> {
        jsReturn += ".createDocumentFragment()"
        return applyMethods(call, HTMLElement(), this)
    }
    fun elements(amount: JsValue<Int>, element: JsValue<Element>, call: (BrowserObject) -> Unit): Reference<DOM> {
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
        return applyMethods(call, HTMLElement(), this)
    }
    fun elements(amount: JsValue<Int>, element: JsValue<HTMLElement>, elementInsides: JsValue<String>, attribute: JsValue<JsObject>, call: (BrowserObject) -> Unit): Reference<DOM> {
        jsReturn = "elements(${amount.toJs()}, ${element}, ${elementInsides}, $attribute)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun body(call: (HTMLElement) -> Unit): Reference<DOM> {
        jsReturn += ".body"
        return applyMethods(call, HTMLElement(), this)
    }
    fun attribute(names: AttributeNames, call: (JsAttribute) -> Unit): Reference<DOM> {
        jsReturn += ".createAttribute(\"${names.name.lowercase()}\")"
        return applyMethods(call, JsAttribute(), this)
    }
    fun attribute(name: JsValue<String>, call: (JsAttribute) -> Unit): Reference<DOM> {
        jsReturn += ".createAttribute($name)"
        return applyMethods(call, JsAttribute(), this)
    }
    fun cookies(nameAndValue: JsValue<String>): Reference<DOM> {
        jsReturn += ".cookie = $nameAndValue"
        return this.refer()
    }
    fun cookies(): JsValue<String> {
        jsReturn += ".cookie"
        return "".asJsValue()
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
    fun clone(children: JsValue<Boolean> = DirectValue(true), call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".cloneNode(${children.toJs()})"
        return applyMethods(call, HTMLElement(), this)
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
    fun attributes(element: JsValue<HTMLElement>, attributes: JsValue<JsObject>): Reference<HTMLElement> {
        jsReturn = "attributes($element, $attributes)"
        return this.refer()
    }
    fun parent(onlyElements: Boolean, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".parent${if (onlyElements) "Element" else "Node"}"
        return applyMethods(call, HTMLElement(), this)
    }
    fun children(call: (JsList<HTMLElement>) -> Unit): Reference<HTMLElement> {
        jsReturn += ".children"
        return applyMethods(call, JsList(HTMLElement().asJsValue()), this)
    }
    fun sibling(next: Boolean, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".${if (next) "next" else "previous"}Sibling"
        return applyMethods(call, HTMLElement(), this)
    }
    fun closest(id: JsValue<String>, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".closest($id)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun matches(id: JsValue<String>): JsValue<Boolean> {
        jsReturn += ".matches($id)"
        return true.asJsValue()
    }
    fun getRect(call: (JsObject) -> Unit): Reference<HTMLElement> {
        jsReturn += ".getBoundingClientRect()"
        return applyMethods(call, JsObject(mapOf()), this)
    }
    fun getOffset(call: (JsObject) -> Unit): Reference<HTMLElement> {
        jsReturn += ".offset()"
        return applyMethods(call, JsObject(mapOf()), this)
    }
    fun getDimensions(call: (JsObject) -> Unit): Reference<HTMLElement> {
        jsReturn += ".getBoundingClientRect()"
        return applyMethods(call, JsObject(mapOf()), this)
    }
    fun style(id: JsValue<String>, value: JsValue<String>): Reference<HTMLElement> {
        jsReturn += ".style.$id = $value"
        return this.refer()
    }
    fun style(id: JsValue<String>): JsValue<String> {
        jsReturn += ".style.$id"
        return "".asJsValue()
    }
    fun selectAll(identifier: JsValue<String>, call: (JsList<HTMLElement>) -> Unit): Reference<HTMLElement> {
        jsReturn += ".querySelectorAll(${identifier.toJs()})"
        return applyMethods(call, JsList(HTMLElement().asJsValue()), this)
    }
    fun before(child: JsValue<HTMLElement>, element: JsValue<HTMLElement>, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".insertBefore($child, $element)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun replace(child: JsValue<HTMLElement>, element: JsValue<HTMLElement>, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".replaceChild($child, $element)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun remove(): Reference<HTMLElement> {
        jsReturn += ".remove()"
        return this.refer()
    }
    fun remove(element: JsValue<HTMLElement>, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".removeChild($element)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun add(element: JsValue<HTMLElement>, call: (HTMLElement) -> Unit): Reference<HTMLElement> {
        jsReturn += ".appendChild($element)"
        return applyMethods(call, HTMLElement(), this)
    }
    fun put(element: JsValue<*>, type: PutType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase()}($element)"
        return this.refer()
    }
    fun replace(element: JsValue<*>, type: ReplaceType): Reference<HTMLElement> {
        jsReturn += ".${type.name.lowercase().uppercase()}($element)"
        return this.refer()
    }
    fun value(): Reference<HTMLElement> {
        jsReturn += ".value"
        return this.refer()
    }
    fun value(value: JsValue<*>): Reference<HTMLElement> {
        jsReturn += ".value = $value"
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

fun JavaScript.id(id: JsValue<String>, document: Variable<DOM>? = null, call: (HTMLElement) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.id(id, call)
    children.add(elements)
    return elements
}
fun JavaScript.cssClass(cssClass: JsValue<String>, document: Variable<DOM>? = null, call: (HTMLElement) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.cssClass(cssClass, call)
    children.add(elements)
    return elements
}
fun JavaScript.name(name: JsValue<String>, document: Variable<DOM>? = null, call: (JsList<HTMLElement>) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.name(name, call)
    children.add(elements)
    return elements
}
fun JavaScript.tag(tag: JsValue<String>, document: Variable<DOM>? = null, call: (JsList<HTMLElement>) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.tag(tag, call)
    children.add(elements)
    return elements
}
fun JavaScript.selectAll(id: JsValue<String>, document: Variable<DOM>? = null, call: (JsList<HTMLElement>) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.selectAll(id, call)
    children.add(elements)
    return elements
}
fun JavaScript.select(id: JsValue<String>, document: Variable<DOM>? = null, call: (HTMLElement) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.select(id, call)
    children.add(elements)
    return elements
}
fun JavaScript.element(element: JsValue<Element>, document: Variable<DOM>? = null, call: (HTMLElement) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.element(element, call)
    children.add(elements)
    return elements
}
fun JavaScript.fragment(document: Variable<DOM>? = null, call: (BrowserObject) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.fragment(call)
    children.add(elements)
    return elements
}
fun JavaScript.elements(amount: JsValue<Int>, element: JsValue<Element>, document: Variable<DOM>? = null, call: (BrowserObject) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.elements(amount, element, call)
    children.add(elements)
    return elements
}
fun JavaScript.elements(amount: JsValue<Int>, element: JsValue<HTMLElement>, elementInsides: JsValue<String>, attribute: JsValue<JsObject>, document: Variable<DOM>? = null, call: (BrowserObject) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.elements(amount, element, elementInsides, attribute, call)
    children.add(elements)
    return elements
}
fun JavaScript.body(document: Variable<DOM>? = null, call: (HTMLElement) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.body(call)
    children.add(elements)
    return elements
}
fun JavaScript.attribute(names: AttributeNames, document: Variable<DOM>? = null, call: (JsAttribute) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.attribute(names, call)
    children.add(elements)
    return elements
}
fun JavaScript.attribute(name: JsValue<String>, document: Variable<DOM>? = null, call: (JsAttribute) -> Unit): Reference<DOM> {
    val dom = DOM(document)
    val elements = dom.attribute(name, call)
    children.add(elements)
    return elements
}
fun JavaScript.cookies(nameAndValue: JsValue<String>, document: Variable<DOM>? = null): Reference<DOM> {
    val dom = DOM(document)
    children.add(dom)
    val elements = dom.cookies(nameAndValue)
    return dom.refer()
}
fun JavaScript.cookies(document: Variable<DOM>? = null): JsValue<String> {
    val dom = DOM(document)
    children.add(dom)
    return dom.cookies()
}
