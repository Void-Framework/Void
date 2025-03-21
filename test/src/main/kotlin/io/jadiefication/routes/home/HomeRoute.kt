package io.jadiefication.routes.home

import io.void.generated.*
import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.js.data.DataHandler
import io.void.js.data.DataHolder
import java.net.URL

class HomeRoute : Page(target = "/") {

    val data = DataHandler.singleton.create("Hello")

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
        H1 {
            Fractal("Main")
            Br()
            Fractal("Title")
        }

        Hr()

        Div(
            attribute = arrayOf(attribute {
                name = AttributeNames.CLASS
                value = "section"
            }),
        ) {
            H2 {
                A(
                    attribute = arrayOf(
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
                        }),
                ) {
                    Fractal(_text = "Click Me Title")
                }
            }

            Br()

            H1 {
                data.get()?.let { Fractal(_text = it) }
            }

            H3 {
                Fractal("Subtitle Here")
            }
        }
    }
}