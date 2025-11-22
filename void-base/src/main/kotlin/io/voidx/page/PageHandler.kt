package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.emptyResponse

/**
 * Lightweight page that dispatches to verb-specific handlers for API-style routes.
 *
 * Use infix functions like `on("/path") GET { ... }` to register handlers per HTTP method.
 */
class PageHandler(
    override val target: String,
) : DynamicPage(
        target = target,
    ) {
    private val responses = mutableMapOf<Method, (RequestDTO) -> ResponseDTO>()

    /** Returns the response from the registered handler for [RequestDTO.method], or an empty response if none. */
    override fun content(): ResponseDTO = responses[request.method]?.invoke(request) ?: emptyResponse()

    /** Registers a GET handler. */
    infix fun GET(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.GET] = body }

    /** Registers a POST handler. */
    infix fun POST(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.POST] = body }

    /** Registers a HEAD handler. */
    infix fun HEAD(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.HEAD] = body }

    /** Registers a PUT handler. */
    infix fun PUT(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.PUT] = body }

    /** Registers a DELETE handler. */
    infix fun DELETE(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.DELETE] = body }

    /** Registers a CONNECT handler. */
    infix fun CONNECT(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.CONNECT] = body }

    /** Registers an OPTIONS handler. */
    infix fun OPTIONS(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.OPTIONS] = body }

    /** Registers a TRACE handler. */
    infix fun TRACE(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.TRACE] = body }

    /** Registers a PATCH handler. */
    infix fun PATCH(body: (RequestDTO) -> ResponseDTO): PageHandler = apply { responses[Method.PATCH] = body }
}