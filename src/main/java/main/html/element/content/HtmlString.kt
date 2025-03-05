package main.html.element.content

import main.html.element.Element
import main.html.element.SelfClosingElement
import main.html.exceptions.ElementException

typealias IntRangePosition = MutableMap<IntRange, InlineElement>
typealias Position = MutableMap<Int, InlineElement>


class HtmlString(private val pos: IntRangePosition, private val text: String) {

    constructor(pos: Position, text: String) : this(pos.mapKeys {
        IntRange(it.key, it.key)
    } as IntRangePosition, text)

    fun convert(): String {
        val result = StringBuilder(text)
        pos.forEach {
            val instance = it.value::class.java.getDeclaredConstructor().newInstance()
            if (instance is Element) {
                if (it.key.first != it.key.last) {
                    if (instance is SelfClosingElement) {
                        result.insert(it.key.first, "<${instance.name}/>")
                        result.insert(it.key.last + instance.name.length + 3, "<${instance.name}/>")
                    } else {
                        result.insert(it.key.first, "<${instance.name}/>")
                        result.insert(it.key.last + instance.name.length + 2, "</${instance.name}>")
                    }
                } else {
                    if (instance is SelfClosingElement) {
                        result.insert(it.key.first, "<${instance.name}/>")
                    } else {
                        result.insert(it.key.first, "<${instance.name}/>")
                        result.insert(it.key.last + instance.name.length + 2, "</${instance.name}>")
                    }
                }
            } else {
                throw ElementException("A class in the map is not an Element")
            }
        }

        return result.toString()
    }
}