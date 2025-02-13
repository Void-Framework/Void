package main.html.element.content

import main.html.element.Element
import main.html.element.SelfClosingElement
import main.html.exceptions.ElementException

class HtmlString(private val pos: Map<Int, InlineElement>, val text: String) {

    fun convert(): String {
        val result = StringBuilder(text)
        pos.forEach {
            val instance = it.value::class.java.getDeclaredConstructor().newInstance()
            if (instance is Element) {
                if (instance is SelfClosingElement) {
                    result.insert(it.key, "<${instance.name}/>")
                } else {
                    result.insert(it.key, "<${instance.name}>")
                    result.insert(it.key + instance.name.length + 2, "</${instance.name}>")
                }
            } else {
                throw ElementException("A class in the map is not an Element")
            }
        }

        return result.toString()
    }
}