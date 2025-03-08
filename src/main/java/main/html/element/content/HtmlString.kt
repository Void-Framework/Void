package main.html.element.content

import main.html.element.Element
import main.html.element.SelfClosingElement
import main.html.exceptions.ElementException

data class TagPosition(
    val range: IntRange,
    val element: InlineElement,
    val children: MutableList<TagPosition>
)

interface InlineElement

class HtmlString(private val text: String) {

    companion object {
        fun create(text: String, builder: HtmlString.() -> Unit): HtmlString {
            return HtmlString(text).apply(builder)
        }
    }

    private fun renderAttributes(element: Element): String {
        val attributesBuilder = StringBuilder("")
        element.attributes.entries.forEach { (name, value) ->
            attributesBuilder.append("${name.name.lowercase()}=\"$value\" ")
        }
        if (element.attributes.isNotEmpty()) {
            attributesBuilder.setLength(attributesBuilder.length - 1)
        }
        return attributesBuilder.toString()
    }

    fun convert(): String {
        val result = StringBuilder(text)
        pos.forEach { (range, element) ->
            when (element) {
                is Element -> {

                    /*val spaces = attrs.length + 1
                    if (range.first != range.last) {
                        when (element) {
                            is SelfClosingElement -> {
                                result.insert(range.first, "<${element.name} ${attrs}/>")
                                result.insert(range.last + element.name.length + 3 + spaces, "<${element.name}/>")
                            }
                            else -> {
                                result.insert(range.first, "<${element.name} ${attrs}>")
                                result.insert(range.last + element.name.length + 2 + spaces, "</${element.name}>")
                            }
                        }
                    } else {
                        when (element) {
                            is SelfClosingElement -> {
                                result.insert(range.first, "<${element.name} ${attrs}/>")
                            }
                            else -> {
                                result.insert(range.first, "<${element.name} ${attrs}>")
                                result.insert(range.last + element.name.length + 2 + spaces, "</${element.name}>")
                            }
                        }
                    }*/
                }
                else -> throw ElementException("A class in the map is not an Element")
            }
        }

        return result.toString()
    }
}