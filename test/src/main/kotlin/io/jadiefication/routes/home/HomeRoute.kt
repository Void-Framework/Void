package io.jadiefication.routes.home

import io.jadiefication.components.TestComponent
import io.jadiefication.components.testComponent
import io.void.generated.*
import io.void.html.Element
import io.void.html.Fragment
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
        H1 {
            Fragment("Main")
            Br()
            Fragment("Title")
        }

        Hr()

        testComponent()
    }
}