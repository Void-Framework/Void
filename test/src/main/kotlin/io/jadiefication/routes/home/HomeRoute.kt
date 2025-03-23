package io.jadiefication.routes.home

import io.jadiefication.components.testComponent
import io.void.generated.Br
import io.void.generated.Div
import io.void.generated.H1
import io.void.generated.Hr
import io.void.html.Element
import io.void.html.Fragment
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page

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