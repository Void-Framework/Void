package main.html.element

import main.html.attributes.AttributeNames
import main.html.element.content.HtmlString

abstract class Element internal constructor(open val name: String) {

    open val children: MutableList<Element>? = mutableListOf()
    val attributes = mutableMapOf<String, String>()
    private val globalAttributes = listOf(AttributeNames.ACCESSKEY, AttributeNames.CLASS, AttributeNames.CONTENTEDITABLE, AttributeNames.DATA,
        AttributeNames.DIR, AttributeNames.DRAGGABLE, AttributeNames.ENTERKEYHINT, AttributeNames.HIDDEN, AttributeNames.ID,
        AttributeNames.INERT, AttributeNames.INPUTMODE, AttributeNames.LANG, AttributeNames.POPOVER, AttributeNames.SPELLCHECK,
        AttributeNames.STYLE, AttributeNames.TABINDEX, AttributeNames.TITLE, AttributeNames.TRANSLATE)
    abstract val allowedAttributes: List<AttributeNames>

    fun isAllowed(attribute: AttributeNames): Boolean {
        return allowedAttributes.contains(attribute) || globalAttributes.contains(attribute)
    }

    abstract fun render(): String

    operator fun set(key: String, value: String) {
        attributes[key] = value
    }

    inline fun <reified T : ElementWithChildren> element(block: T.() -> Unit): T {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        instance.apply(block)
        children!!.add(instance)  // Add to the parent's children
        return instance
    }

    inline fun <reified T : TextElement> textElement(text: HtmlString): T {
        val instance = T::class.java.getDeclaredConstructor(HtmlString::class.java).newInstance(text)
        children!!.add(instance)  // Add to the parent's children
        return instance
    }

    inline fun <reified T : SelfClosingElement> selfClosingElement(): T {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        children!!.add(instance)  // Add to the parent's children
        return instance
    }
}