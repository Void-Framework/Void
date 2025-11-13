package io.void.html.util

import io.void.dto.http.ResponseDTO
import io.void.dto.http.ok
import io.void.html.Element
import io.void.html.metadata.Metadata

/**
 * Builds a full HTML [ResponseDTO] for a page consisting of the provided [element]
 * and the page [metadata]. The response is returned with a Content-Type of
 * "text/html" and the original root [Element] is stashed under
 * `attributes["Element"]` so downstream processors (e.g., Tailwind extraction,
 * KTS handlers) can traverse the DOM tree if needed.
 */
fun createResponse(element: Element, metadata: Metadata): ResponseDTO = ok("""
                        <!doctype html><html>
                        <head>${metadata.render()}</head>
                        <body>${element.render()}</body>
                        </html>
                        """.trimIndent(), mutableMapOf("Content-Type" to "text/html"))
    .apply {
        attributes["Element"] = element
    }

/**
 * Convenience overload that wraps a raw [element] into an HTML page response
 * without additional metadata. Sets Content-Type to "text/html" and stores the
 * root [Element] in `attributes["Element"]` for further processing.
 */
fun createResponse(element: Element): ResponseDTO = ok(element.render().trimIndent(), mutableMapOf("Content-Type" to "text/html"))
    .apply {
        attributes["Element"] = element
    }