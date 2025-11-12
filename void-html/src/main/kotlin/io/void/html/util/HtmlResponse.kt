package io.void.html.util

import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.dto.http.ok
import io.void.html.Element
import io.void.html.page.metadata.Metadata

fun createResponse(element: Element, metadata: Metadata): ResponseDTO = ok("""
                        <!doctype html><html>
                        <head>${metadata.render()}</head>
                        <body>${element.render()}</body>
                        </html>
                        """.trimIndent(), mutableMapOf("Content-Type" to "text/html"))
    .apply {
        attributes["Element"] = element
    }


fun createResponse(element: Element): ResponseDTO = ok(element.render().trimIndent(), mutableMapOf("Content-Type" to "text/html"))
    .apply {
        attributes["Element"] = element
    }