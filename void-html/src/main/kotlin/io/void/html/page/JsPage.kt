package io.void.html.page

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.metadata.metadata
import java.util.UUID

/**
 * Internal page that serves a generated JavaScript asset at a unique UUID-backed path.
 * The router loads scripts from resources/js and exposes them via these pages.
 */
internal class JsPage(
    uuid: UUID,
    private val body: String,
) : Page(
        target = "/js/$uuid/script.js",
    ) {
    override fun content(): ResponseDTO =
        if (request.method == Method.GET) {
            buildResponse {
                status = 200
                statusText = "All is Well"
                headers {
                    put("Content-Type", "text/javascript")
                    put("X-Content-Type-Options", "nosniff")
                }
                body = this@JsPage.body
            }
        } else {
            buildResponse<String> {
                status = 405
                statusText = "Method not allowed"
            }
        }

    /** Utility functions to attach the JS page into HTML page metadata. */
    companion object {
        fun addToMetadata(
            page: Page,
            jsPage: JsPage,
        ) {
            if (page.metadata == null) {
                page.metadata =
                    metadata(page) {
                        externalJS = mutableMapOf(jsPage.target to true)
                    }
            } else {
                val meta = page.metadata!!
                if (meta.externalJS == null) {
                    meta.externalJS = mutableMapOf()
                }
                meta.externalJS!![jsPage.target] = true
            }
        }

        fun addToMetadata(
            page: Page,
            jsPage: List<JsPage>,
        ) {
            jsPage.forEach {
                addToMetadata(page, it)
            }
        }
    }
}
