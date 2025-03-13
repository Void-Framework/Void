package io.jadiefication.routes.home

import io.void.generated.A
import io.void.generated.Br
import io.void.generated.Div
import io.void.generated.Hr
import io.void.html.Element
import io.void.html.H1
import io.void.html.H2
import io.void.html.H3
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
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
            text = "Main${Br().render()} Title"
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
                text = "${
                    A(
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
                ).render()}Click Me Title"
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