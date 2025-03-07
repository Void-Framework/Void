package main.html.element

import main.html.element.content.HtmlString

abstract class TextElement internal constructor(name: String, private var text: HtmlString?) : Element(name) {

    override fun render(): String {
        val attrs = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
        return "<$name $attrs>${text!!.convert()}</$name>"
    }

    fun setText(_text: HtmlString) {
        text = _text
    }
}
