package main.html.element.content

import main.html.attributes.Attribute
import main.html.attributes.AttributeNames
import main.html.element.TextElement

interface HElement

class H1(text: HtmlString? = null, vararg attributes: Attribute): TextElement("h1", text = text), HElement {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H2(text: HtmlString? = null, vararg attributes: Attribute) : TextElement("h2", text = text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H3(text: HtmlString? = null, vararg attributes: Attribute) : TextElement("h3", text = text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H4(text: HtmlString? = null, vararg attributes: Attribute) : TextElement("h4", text =  text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H5(text: HtmlString? = null, vararg attributes: Attribute) : TextElement("h5", text =  text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H6(text: HtmlString? = null, vararg attributes: Attribute) : TextElement("h6", text =  text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}