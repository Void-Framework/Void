package io.void.api

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.MetadataHandler
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
                ResponseDTO(
                    status = 200,
                    statusText = "All is Well",
                    headers =
                        mutableMapOf(
                            "Content-Type" to "text/js",
                        ),
                    body = body,
                )
            } else {
                ResponseDTO(
                    status = 405,
                    statusText = "Method not allowed",
                    headers = mutableMapOf(),
                    body = "",
                )
            },
        )

    companion object {
        fun addToMetadata(
            page: Page<ContentType.HtmlElements>,
            jsPage: JsPage,
        ) {
            if (page.metadata == null) {
                page.metadata =
                    MetadataHandler.create(page = page, builder = {
                        externalJS = mutableMapOf(jsPage.target to true)
                    })
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
