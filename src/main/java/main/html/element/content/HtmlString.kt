package main.html.element.content

import main.html.element.Element
import main.html.element.SelfClosingElement
import main.html.exceptions.ElementException

typealias IntRangePosition = MutableMap<IntRange, InlineElement>
typealias Position = MutableMap<Int, InlineElement>

interface InlineElement

class HtmlString(private val pos: IntRangePosition, private val text: String) {

    companion object {
        fun fromSinglePositions(pos: Position, text: String): HtmlString {
            return HtmlString(pos.mapKeys {
                IntRange(it.key, it.key)
            }.toMutableMap()
                , text)
        }

        fun fromRanges(pos: IntRangePosition, text: String): HtmlString {
            return HtmlString(pos, text)
        }
    }

    fun convert(): String {
        val result = StringBuilder(text)
        pos.forEach { (range, element) ->
            when (element) {
                is Element -> {
                    var attrs: String = ""
                    element.attributes.entries.forEach { (name, value) ->
                        attrs += "${name.name.lowercase()}=\"$value\" "
                    }
                    val spaces = attrs.length + 1
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
                    }
                }
                else -> throw ElementException("A class in the map is not an Element")
            }
        }

        return result.toString()
    }
}