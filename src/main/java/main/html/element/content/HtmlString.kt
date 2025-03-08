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

    private val tags = mutableListOf<TagPosition>()

    companion object {
        fun create(text: String, builder: HtmlString.() -> Unit): HtmlString {
            return HtmlString(text).apply(builder)
        }
    }

    fun addTag(range: IntRange, element: InlineElement) {
        // Find parent tag if this range is nested
        val parent = findParentTag(range)
        val newTag = TagPosition(range, element, mutableListOf())

        if (parent != null) {
            parent.children.add(newTag)
        } else {
            tags.add(newTag)
        }
    }

    private fun findParentTag(range: IntRange): TagPosition? {
        return tags.firstOrNull { parentTag ->
            range.first > parentTag.range.first && range.last < parentTag.range.last
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

    private fun renderTag(position: TagPosition, textContent: String): String {
        val element = position.element
        if (element !is Element) throw ElementException("Invalid element type")

        val tagName = element.name.lowercase()
        val attributes = renderAttributes(element)

        return when (element) {
            is SelfClosingElement -> "<$tagName $attributes/>"
            else -> {
                val content = if (position.children.isEmpty()) {
                    textContent.substring(position.range)
                } else {
                    var result = textContent.substring(position.range)
                    // Process children from innermost to outermost
                    position.children.sortedByDescending { it.range.first }.forEach { child ->
                        result = renderTag(child, result)
                    }
                    result
                }
                "<$tagName $attributes>$content</$tagName>"
            }
        }
    }

    fun convert(): String {
        var result = text
        // Process tags from outermost to innermost
        tags.sortedBy { it.range.first }.forEach { tag ->
            result = renderTag(tag, result)
        }
        return result
    }
}