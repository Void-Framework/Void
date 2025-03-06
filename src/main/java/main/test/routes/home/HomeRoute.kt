package main.test.routes.home

import main.html.attributes.AtrributeTypes
import main.html.attributes.attribute
import main.html.element.Element
import main.html.element.content.Div
import main.html.element.content.HtmlString
import main.html.element.content.formatting.Br
import main.html.element.content.h.H1
import main.html.page.Page

val divAtt = attribute {
    name = "class"
    type = AtrributeTypes.STRING
}

class HomeRoute: Page(
    target = "/"
) {

    override var content: Element = Div(divAtt) {
        textElement<H1>(HtmlString.fromSinglePositions(mutableMapOf(5 to Br()), "Hello, How are you"))
        selfClosingElement<Br>()
    }
}