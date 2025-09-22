package io.void.api

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import java.util.UUID
import kotlin.reflect.KClass

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
                        put("Content-Type", "text/js")
                    }
                    body = this@JsPage.body
                }
            } else {
                buildResponse {
                    status = 405
                    statusText = "Method not allowed"
                }
            },
        )

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
