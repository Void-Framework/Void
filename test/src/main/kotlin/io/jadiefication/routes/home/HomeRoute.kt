package io.jadiefication.routes.home

import io.jadiefication.components.testComponent
import io.void.cache.Cacheable
import io.void.generated.*
import io.void.html.Fragment
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.MetadataHandler
import java.net.URL
import kotlin.reflect.KClass

@Cacheable(invalidationDurationInMillies = 0)
class HomeRoute : Page<ContentType.HtmlElements>(target = "/") {

    override val contentType: KClass<ContentType.HtmlElements> = ContentType.HtmlElements::class
    override val metadata: Metadata = MetadataHandler.create(
        page = this,
        builder = {

        }
    )

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
            testComponent()
        })
    }
}