package io.jadiefication.routes.home

import io.void.generated.*
import io.void.html.Fragment
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import java.net.URL
import kotlin.reflect.KClass

class HomeRoute : Page<ContentType.HtmlElements>(target = "/") {

    override val contentType: KClass<ContentType.HtmlElements> = ContentType.HtmlElements::class

    override fun content(): ContentType.HtmlElements {
        return ContentType.HtmlElements(
            Div(
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
                Fragment("Main")
                Br()
                Fragment("Title")
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
                        Fragment(_text = "Click Me Title")
                    }
                }

                Br()

                H3 {
                    Fragment("Subtitle Here")
                }
            }
        })
    }

}