package io.void.html.element.content

import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.html.element.TextElement

interface HElement

class H1(text: String? = null, vararg attributes: Attribute): TextElement("h1", text = text), HElement {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H2(text: String? = null, vararg attributes: Attribute) : TextElement("h2", text = text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H3(text: String? = null, vararg attributes: Attribute) : TextElement("h3", text = text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H4(text: String? = null, vararg attributes: Attribute) : TextElement("h4", text =  text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H5(text: String? = null, vararg attributes: Attribute) : TextElement("h5", text =  text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}
class H6(text: String? = null, vararg attributes: Attribute) : TextElement("h6", text =  text), HElement  {
    override val allowedAttributes: List<AttributeNames> = listOf()

    init {
        addAttributes(*attributes)
    }
}