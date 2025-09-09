package io.void.api

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.MetadataHandler
import java.util.*
import kotlin.reflect.KClass

internal class CssPage(uuid: UUID, private val body: String) : Page<ContentType.Response>(
    target = "/css/$uuid/styles.css",
) {

    override val contentType: KClass<ContentType.Response> = ContentType.Response::class
    override var metadata: Metadata? = null

    override fun content(): ContentType.Response {
        return ContentType.Response(
            if (request.method == Method.GET) {
                ResponseDTO(
                    status = 200,
                    statusText = "All is Well",
                    headers = mutableMapOf(
                        "Content-Type" to "text/css",
                    ),
                    body = body
                )
            } else {
                ResponseDTO(
                    status = 405,
                    statusText = "Method not allowed",
                    headers = mutableMapOf(),
                    body = ""
                )
            }
        )
    }
}