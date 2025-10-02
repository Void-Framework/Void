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

    fun get(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.GET] = body }
    fun post(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.POST] = body }
    fun head(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.HEAD] = body }
    fun put(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.PUT] = body }
    fun delete(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.DELETE] = body }
    fun connect(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.CONNECT] = body }
    fun options(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.OPTIONS] = body }
    fun trace(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.TRACE] = body }
    fun patch(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.PATCH] = body }
}