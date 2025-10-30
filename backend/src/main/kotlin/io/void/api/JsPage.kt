package io.void.api

import io.void.api.method.Method
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import java.util.*
import kotlin.reflect.KClass

/**
 * Internal page that serves a generated JavaScript asset at a unique UUID-backed path.
 * The router loads scripts from resources/js and exposes them via these pages.
 */
internal class JsPage(
    uuid: UUID,
    private val body: String,
) : Page<ContentType.Response>(
        target = "/js/$uuid/script.js",
    ) {
    override val contentType: KClass<ContentType.Response> = ContentType.Response::class
    override var metadata: Metadata? = null

    override fun content(): ContentType.Response =
        ContentType.Response(
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
            },
        )

    /** Utility functions to attach the JS page into HTML page metadata. */
    companion object {
        fun addToMetadata(
            page: Page<ContentType.HtmlElements>,
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
            page: Page<ContentType.HtmlElements>,
            jsPage: List<JsPage>,
        ) {
            jsPage.forEach {
                addToMetadata(page, it)
            }
        }
    }
}
