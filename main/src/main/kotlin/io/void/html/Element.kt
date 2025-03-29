package io.void.html

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames

abstract class Element internal constructor(open val name: String) {

    open val children: MutableList<Element>? = mutableListOf()
    val attributes = mutableMapOf<AttributeNames, String>()
    private val globalAttributes = listOf(AttributeNames.ACCESSKEY, AttributeNames.CLASS, AttributeNames.CONTENTEDITABLE, AttributeNames.DATA,
        AttributeNames.DIR, AttributeNames.DRAGGABLE, AttributeNames.ENTERKEYHINT, AttributeNames.HIDDEN, AttributeNames.ID,
        AttributeNames.INERT, AttributeNames.INPUTMODE, AttributeNames.LANG, AttributeNames.POPOVER, AttributeNames.SPELLCHECK,
        AttributeNames.STYLE, AttributeNames.TABINDEX, AttributeNames.TITLE, AttributeNames.TRANSLATE)
    abstract val allowedAttributes: List<AttributeNames>

    private fun isAllowed(attribute: AttributeNames): Boolean {
        return allowedAttributes.contains(element = attribute) || globalAttributes.contains(element = attribute)
    }

    abstract fun render(): String

    fun addAttributes(vararg _attributes: Attribute) {
        _attributes.forEach {
            if (isAllowed(attribute = it.name) && it.isCorrectValue()) {
                attributes[it.name] = it.value.toString()
        } }
    }
}