package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.emptyResponse
import io.voidx.json.Negotiator
import io.voidx.dto.buildResponse
import io.voidx.router.util.RouteCheck

/**
 * Lightweight page that dispatches to verb-specific handlers for API-style routes.
 *
 * Use infix functions like `on("/path") GET { ... }` to register handlers per HTTP method.
 */
open class PageHandler(
    override val target: String,
) : DynamicPage(
        target = target,
    ) {
    val responses =
        mutableMapOf<Method, Negotiator.() -> ResponseDTO>().apply {
            Method.entries.forEach {
                put(it) {
                    buildResponse {
                        status = 405
                        statusText = "Method Not Allowed"
                        body = "Method Not Allowed"
                    }
                }
            }
        }

    /** Returns the response from the registered handler for [RequestDTO.method], or an empty response if none. */
    override fun content(): ResponseDTO =
        responses[request.method]?.invoke(Negotiator(request)) ?: RouteCheck.nullPage.apply { this.request = this@PageHandler.request }.content()

    /** Registers a GET handler. */
    infix fun GET(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.GET] = body }

    /** Registers a POST handler. */
    infix fun POST(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.POST] = body }

    /** Registers a HEAD handler. */
    infix fun HEAD(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.HEAD] = body }

    /** Registers a PUT handler. */
    infix fun PUT(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.PUT] = body }

    /** Registers a DELETE handler. */
    infix fun DELETE(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.DELETE] = body }

    /** Registers a CONNECT handler. */
    infix fun CONNECT(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.CONNECT] = body }

    /** Registers an OPTIONS handler. */
    infix fun OPTIONS(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.OPTIONS] = body }

    /** Registers a TRACE handler. */
    infix fun TRACE(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.TRACE] = body }

    /** Registers a PATCH handler. */
    infix fun PATCH(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.PATCH] = body }
}
