package io.void.router.page

import io.void.api.method.Method
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.dto.http.emptyResponse
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

class PageHandler(
    override val target: String
) : Page<ContentType.Response>(
    target = target
) {
    override var metadata: Metadata? = null
    override val contentType: KClass<ContentType.Response> = ContentType.Response::class
    private val responses = mutableMapOf<Method, (RequestDTO) -> ResponseDTO>()

    override fun content(): ContentType.Response {
        return ContentType.Response(responses[request.method]?.invoke(request) ?: emptyResponse())
    }

    infix fun GET(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.GET] = body }
    infix fun POST(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.POST] = body }
    infix fun HEAD(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.HEAD] = body }
    infix fun PUT(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.PUT] = body }
    infix fun DELETE(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.DELETE] = body }
    infix fun CONNECT(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.CONNECT] = body }
    infix fun OPTIONS(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.OPTIONS] = body }
    infix fun TRACE(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.TRACE] = body }
    infix fun PATCH(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.PATCH] = body }
}