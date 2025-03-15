package io.void.html

@Deprecated(
    message = "No need for this class as of the moment, might be deleted in future commits.",
    replaceWith = ReplaceWith(
        expression = "Use ElementWithChildren, since most of the supported elements support both text and child elements.",
        imports = arrayOf("ElementWithChildren")
    ),
    level = DeprecationLevel.HIDDEN
)
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
