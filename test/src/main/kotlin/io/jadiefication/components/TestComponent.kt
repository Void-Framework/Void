package io.jadiefication.components

import io.void.generated.*
import io.void.html.CustomElement
import io.void.html.Element
import io.void.html.Fragment
import io.void.html.attributes.Attribute
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.loop
import java.net.URL

class TestComponent: CustomElement() {

    override val element: Element = Div(
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

        listOf("test", "test").forEach {
            H1 {
                Fragment(it)
            }
            Br()
        }

        loop(0..3) {
            H1 {
                Fragment("HELP $it")
            }
            Br()
        }
    }
}

fun Element.testComponent(): TestComponent {
    val testComponent = TestComponent()
    children!!.add(testComponent)
    return testComponent
}