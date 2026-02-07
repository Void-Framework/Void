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

    /**
         * Dispatches the current request to the handler registered for its HTTP method and returns the handler's response.
         *
         * The registered handler is invoked with a Negotiator receiver constructed from the current request.
         * If no handler is registered for the request method, delegates to RouteCheck.nullPage (after assigning its request) and returns that page's content.
         *
         * @return The ResponseDTO produced by the method handler, or the content of RouteCheck.nullPage when no handler is present.
         */
    override fun content(): ResponseDTO =
        responses[request.method]?.invoke(Negotiator(request)) ?: RouteCheck.nullPage.apply { this.request = this@PageHandler.request }.content()

    /**
 * Register a handler invoked for HTTP GET requests on this page.
 *
 * @param body The handler executed with a Negotiator receiver to produce the ResponseDTO for GET requests.
 * @return This PageHandler to allow fluent chaining.
 */
    infix fun GET(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.GET] = body }

    /**
 * Register a handler for HTTP POST requests on this page.
 *
 * @param body A handler executed with a `Negotiator` receiver that produces the `ResponseDTO` for POST requests.
 * @return This `PageHandler` instance to allow fluent chaining of registrations.
 */
    infix fun POST(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.POST] = body }

    /**
 * Register a handler for HTTP HEAD requests on this page.
 *
 * @param body A handler executed with a `Negotiator` receiver that produces the response `ResponseDTO`.
 * @return This `PageHandler` instance to allow fluent chaining.
 */
    infix fun HEAD(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.HEAD] = body }

    /**
 * Register a handler to be invoked for HTTP PUT requests on this page.
 *
 * @param body A handler function with a Negotiator receiver that produces the response for PUT requests.
 * @return This PageHandler instance for fluent chaining.
 */
    infix fun PUT(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.PUT] = body }

    /**
 * Registers a handler for HTTP DELETE requests on this page.
 *
 * @param body The handler invoked for DELETE requests; executed with a `Negotiator` receiver and must return a `ResponseDTO`.
 * @return This `PageHandler` instance to allow fluent chaining.
 */
    infix fun DELETE(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.DELETE] = body }

    /**
 * Register a handler for HTTP CONNECT requests.
 *
 * @param body Handler invoked with a Negotiator receiver for CONNECT requests; must return the response DTO.
 * @return This PageHandler instance to allow fluent chaining.
 */
    infix fun CONNECT(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.CONNECT] = body }

    /**
 * Registers a handler for HTTP OPTIONS requests on this PageHandler.
 *
 * @param body Lambda executed with a Negotiator receiver that produces the ResponseDTO for OPTIONS requests.
 * @return This PageHandler instance to allow fluent chaining.
 */
    infix fun OPTIONS(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.OPTIONS] = body }

    /**
 * Register a handler for HTTP TRACE requests on this page.
 *
 * @param body Handler executed with a Negotiator receiver to produce the response for TRACE requests.
 * @return This PageHandler instance to allow fluent chaining.
 */
    infix fun TRACE(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.TRACE] = body }

    /**
 * Register a handler to handle HTTP PATCH requests for this page.
 *
 * @param body Handler invoked with a `Negotiator` receiver to produce the response for a PATCH request.
 * @return This `PageHandler` instance to allow fluent chaining.
 */
    infix fun PATCH(body: Negotiator.() -> ResponseDTO): PageHandler = apply { responses[Method.PATCH] = body }
}