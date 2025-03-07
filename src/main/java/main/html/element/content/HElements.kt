package main.html.element.content

import main.html.attributes.AttributeNames
import main.html.element.TextElement


class H1(text: HtmlString? = null): TextElement("h1", text = text) {
    override val allowedAttributes: List<AttributeNames> = listOf()
}
class H2(text: HtmlString? = null) : TextElement("h2", text = text) {
    override val allowedAttributes: List<AttributeNames> = listOf()
}
class H3(text: HtmlString? = null) : TextElement("h3", text = text) {
    override val allowedAttributes: List<AttributeNames> = listOf()
}
class H4(text: HtmlString? = null) : TextElement("h4", text =  text) {
    override val allowedAttributes: List<AttributeNames> = listOf()
}
class H5(text: HtmlString? = null) : TextElement("h5", text =  text) {
    override val allowedAttributes: List<AttributeNames> = listOf()
}
class H6(text: HtmlString? = null) : TextElement("h6", text =  text) {
    override val allowedAttributes: List<AttributeNames> = listOf()
}