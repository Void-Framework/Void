package io.void.api

import io.void.api.method.Method
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import java.util.*
import kotlin.reflect.KClass

/**
 * Internal page that serves a generated CSS asset at a unique UUID-backed path.
 * Used by the router to expose styles discovered under resources/css and added to page metadata.
 */
internal class CssPage(
    uuid: UUID,
    private val body: String,
) : Page<ContentType.Response>(
        target = "/css/$uuid/styles.css",
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
                        put("Content-Type", "text/css")
                    }
                    this.body = this@CssPage.body
                }
            } else {
                buildResponse<String> {
                    status = 405
                    statusText = "Method not allowed"
                }
            },
        )
}
