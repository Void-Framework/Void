package io.void.html.page

import io.void.html.Element
import io.void.html.attributes.AttributeNames
import io.void.js.EventDispatcher
import io.void.js.data.DataHolder

abstract class Page(open val target: String) {

    abstract var content: Element?
    private val classAttributes: MutableMap<Element, List<String>> = mutableMapOf()

    init {
        if (content != null) {
            if (content!!.attributes.containsKey(AttributeNames.CLASS)) {
                putInTailwind(content!!)
            }
            content!!.children?.forEach {
                putInTailwind(it)
            }
        }
        EventDispatcher.addEventListener(DataHolder::class.java) {
            println(it.get())
        }
    }

    private fun putInTailwind(element: Element) {
        if (element.attributes.containsKey(AttributeNames.CLASS)) {
            classAttributes[element] = element.attributes[AttributeNames.CLASS]!!.split(" ")
        }
    }
}