package main.test.routes.home

import main.html.attributes.AttributeNames
import main.html.attributes.AttributeTypes
import main.html.attributes.attribute
import main.html.element.Element
import main.html.element.content.*
import main.html.element.content.formatting.Br
import main.html.element.content.formatting.Hr
import main.html.page.Page
import java.net.URL

class HomeRoute : Page(target = "/") {
    override var content: Element? = Div(
        attribute {
            name = AttributeNames.CLASS
            value = "main-container"
        },
        attribute {
            name = AttributeNames.ID
            value = "content"
        }
    ) {
        text<H1>(
            attribute = arrayOf(),
            type = H1(),
            text = "Main${Br()} Title"
        )

        selfClosingElement<Hr>(
            type = Hr()
        )

        div(
            attribute {
                name = AttributeNames.CLASS
                value = "section"
            }
        ) {
            text<H2>(
                attribute = arrayOf(),
                type = H2(),
                text = "${A(
                    attribute {
                        name = AttributeNames.HREF
                        value = URL("https://example.com")
                    },
                    attribute {
                        name = AttributeNames.TARGET
                        value = "_blank"
                    },
                    attribute {
                        name = AttributeNames.REL
                        value = "noopener"
                    },
                    function = { }
                )}Click Me Title"
            )

            selfClosingElement<Br>(
                type = Br()
            )

            text<H3>(
                attribute = arrayOf(),
                type = H3(),
                text = "Subtitle Here"
            )
        }
    }
}