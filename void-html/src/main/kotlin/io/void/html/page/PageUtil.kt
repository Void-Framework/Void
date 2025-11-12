package io.void.html.page

import io.void.dto.http.RequestDTO
import io.void.html.Element
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import io.void.html.util.createResponse

/**
 * Defines an HTML route at [path] with page-level [metadata] and a content [block]
 * that builds and returns the root [Element] for the response body.
 */
fun htmlRoute(
    path: String,
    metadata: Metadata.() -> Unit,
    block: Page.(RequestDTO) -> Element,
): Page =
    object : Page(target = path) {
        override var metadata: Metadata? = metadata(this) { }.apply(metadata)

        override fun content() = createResponse(block(request), this.metadata!!)
    }


/**
 * Defines a page to render when an exception occurs, producing HTML content with [metadata].
 */
fun exceptionPage(
    metadata: Metadata.() -> Unit,
    block: ExceptionPage.(Exception) -> Element,
): ExceptionPage =
    object : ExceptionPage() {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata

        override fun content() = createResponse(block(exception), this.metadata!!)
    }


fun notFoundPage(
    metadata: Metadata.() -> Unit,
    block: NotFoundPage.(RequestDTO) -> Element,
): NotFoundPage =
    object : NotFoundPage() {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata

        override fun content() = createResponse(block(request), this.metadata!!)
    }