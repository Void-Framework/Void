package io.void.html

import io.void.generated.A
import io.void.generated.Br
import io.void.generated.Div
import io.void.generated.Hr
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.exception.UnsupportedTypeException

abstract class Element internal constructor(open val name: String) {

    open val children: MutableList<Element>? = mutableListOf()
    val attributes = mutableMapOf<AttributeNames, String>()
    private val globalAttributes = listOf(AttributeNames.ACCESSKEY, AttributeNames.CLASS, AttributeNames.CONTENTEDITABLE, AttributeNames.DATA,
        AttributeNames.DIR, AttributeNames.DRAGGABLE, AttributeNames.ENTERKEYHINT, AttributeNames.HIDDEN, AttributeNames.ID,
        AttributeNames.INERT, AttributeNames.INPUTMODE, AttributeNames.LANG, AttributeNames.POPOVER, AttributeNames.SPELLCHECK,
        AttributeNames.STYLE, AttributeNames.TABINDEX, AttributeNames.TITLE, AttributeNames.TRANSLATE)
    abstract val allowedAttributes: List<AttributeNames>

    fun isAllowed(attribute: AttributeNames): Boolean {
        return allowedAttributes.contains(attribute) || globalAttributes.contains(attribute)
    }

    abstract fun render(): String

    fun addAttributes(vararg _attributes: Attribute) {
        _attributes.forEach {
            if (isAllowed(it.name) && it.isCorrectValue()) {
                attributes[it.name] = it.value.toString()
        } }
    }


    fun div(vararg attribute: Attribute, function : Element.() -> Unit): Div {
        val div = Div(
            attributes = attribute,
            function = function)
        children!!.add(div)
        return div
    }

    fun a(vararg attribute: Attribute, function : Element.() -> Unit): A {
        val a = A(
            attributes = attribute,
            function = function)
        children!!.add(a)
        return a
    }

    inline fun <reified T : HElement> text(vararg attribute: Attribute, text: String?, type: T): HElement {
        val _text = when (type) {
            is H1 -> H1(
                text = text,
                attributes = attribute
            )
            is H2 -> H2(
                text = text,
                attributes = attribute
            )
            is H3 -> H3(
                text = text,
                attributes = attribute
            )
            is H4 -> H4(
                text = text,
                attributes = attribute
            )
            is H5 -> H5(
                text = text,
                attributes = attribute
            )
            is H6 -> H6(
                text = text,
                attributes = attribute
            )
            else -> {
                throw UnsupportedTypeException()
            }
        }
        children!!.add(_text)
        return _text
    }

    inline fun <reified T : SelfClosingElement> selfClosingElement(vararg attribute: Attribute, type: T): SelfClosingElement {
        val element = when (type) {
            is Br -> Br(
                attribute = attribute
            )
            is Hr -> Hr(
                attribute = attribute
            )
            else -> {
                throw UnsupportedTypeException()
            }
        }
        children!!.add(element)
        return element
    }
}