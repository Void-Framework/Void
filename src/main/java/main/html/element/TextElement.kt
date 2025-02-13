package main.html.element

import main.html.element.content.HtmlString
import javax.swing.text.html.HTML

abstract class TextElement(name: String, private var text: HtmlString) : Element(name) {

    override fun render(): String {
        val attrs = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        val content = children?.joinToString("") { it.render() } ?: ""
        return "<$name $attrs>${text.convert()}</$name>"
    }

    fun setText(_text: HtmlString) {
        text = _text
    }
}
