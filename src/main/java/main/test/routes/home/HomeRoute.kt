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
            text = HtmlString.fromSinglePositions(
            mutableMapOf(6 to Br()),
            "Main Title"
        ))

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
                text = HtmlString.fromRanges(
                mutableMapOf(
                    IntRange(0, 13) to A(
                        attribute {
                            name = AttributeNames.HREF
                            value = URL("https://example.com").toString()
                        },
                        attribute {
                            name = AttributeNames.TARGET
                            value = "_blank"
                        },
                        attribute {
                            name = AttributeNames.REL
                            value = "noopener"
                        }
                    ) { }
                ),
                "Click Me Title"
            ))

            selfClosingElement<Br>(
                type = Br()
            )

            text<H3>(
                attribute = arrayOf(),
                type = H3(),
                text = HtmlString.fromSinglePositions(
                mutableMapOf(),
                "Subtitle Here"
            ))
        }
    }
}