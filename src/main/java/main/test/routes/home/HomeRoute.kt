package main.test.routes.home

import main.html.attributes.AtrributeTypes
import main.html.attributes.attribute
import main.html.element.Element
import main.html.element.content.*
import main.html.element.content.formatting.Br
import main.html.element.content.h.H1
import main.html.page.Page
import main.router.Route

val divAtt = attribute {
    name = "class"
    type = AtrributeTypes.STRING
}

@Route(target = "/")
class HomeRoute: Page() {

    override var content: Element = Div(divAtt) {
        textElement<H1>(HtmlString(mapOf(Pair(5, Br())), "Hello, How are you"))
        selfClosingElement<Br>()
    }
}