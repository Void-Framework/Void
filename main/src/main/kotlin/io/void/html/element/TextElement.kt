package io.void.html.element

abstract class TextElement internal constructor(name: String, private var text: String?) : Element(name) {

    override fun render(): String {
        var attrs: String = ""
        attributes.entries.forEach { (name, value) ->
            attrs += "${name.name.lowercase()}=\"$value\" "
        }
        return "<$name $attrs>${text!!}</$name>"
    }

    fun setText(_text: String) {
        text = _text
    }
}
