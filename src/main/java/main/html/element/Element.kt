package main.html.element

import main.html.element.content.HtmlString

abstract class Element(open val name: String) {

    open val children: MutableList<Element>? = mutableListOf()
    val attributes = mutableMapOf<String, String>()

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