package io.jadiefication.routes.home

import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.element.Element
import io.void.html.element.content.*
import io.void.html.element.content.formatting.Br
import io.void.html.element.content.formatting.Hr
import io.void.html.page.Page
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