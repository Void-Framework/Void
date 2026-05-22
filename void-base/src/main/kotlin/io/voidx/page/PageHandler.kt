package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.json.Negotiator
import io.voidx.router.CustomPages

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
        mutableMapOf<Method, Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO>().apply {
            Method.entries.forEach {
                put(it) { _, _ ->
                    buildResponse {
                        status = 405
                        statusText = "Method Not Allowed"
                        body = "Method Not Allowed"
                    }
                }
            }
        }

    /**
         * Dispatches the request to the handler registered for its HTTP method and returns the resulting response.
         *
         * The registered handler is invoked with a `Negotiator` receiver created from the provided `request`.
         * If no handler is registered for the request method, delegates to `CustomPages.nullPage.content(request, queries)`.
         *
         * @param request The incoming request to dispatch.
         * @param queries Map of query parameter names to values extracted for this request.
         * @return The `ResponseDTO` produced by the invoked handler or by `CustomPages.nullPage` when no handler is present.
         */
    override fun content(
        request: RequestDTO,
        queries: Map<String, String>,
    ): ResponseDTO = responses[request.method]?.invoke(Negotiator(request), request, queries)
        ?: CustomPages.nullPage.content(request, queries)

    /**
 * Registers the handler to invoke for HTTP GET requests on this page.
 *
 * @param body Handler invoked with a `Negotiator` receiver and arguments `(request, queries)` to produce the `ResponseDTO`.
 * @return This `PageHandler` to allow fluent chaining.
 */
    infix fun GET(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.GET] = body }

    /**
 * Register a handler for HTTP POST requests on this page.
 *
 * @param body Handler invoked with a `Negotiator` receiver, the incoming `RequestDTO`, and the parsed query parameters; it must produce the `ResponseDTO` for the POST request.
 * @return This `PageHandler` instance for fluent chaining of registrations.
 */
    infix fun POST(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.POST] = body }

    /**
 * Register a handler for HTTP HEAD requests on this page.
 *
 * @param body Handler invoked with a `Negotiator` receiver, receiving the incoming `RequestDTO` and a map of query parameters (`Map<String, String>`), and returning the response `ResponseDTO`.
 * @return This `PageHandler` instance for fluent chaining.
 */
    infix fun HEAD(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.HEAD] = body }

    /**
 * Registers a handler for HTTP PUT requests on this page.
 *
 * The handler is invoked with a `Negotiator` receiver and is given the incoming `RequestDTO`
 * and a map of query parameters.
 *
 * @param body Handler invoked for PUT requests; receives the request and query parameters and must produce a `ResponseDTO`.
 * @return This `PageHandler` instance to allow fluent chaining.
 */
    infix fun PUT(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.PUT] = body }

    /**
     * Registers a handler for HTTP DELETE requests on this page.
     *
     * @param body The handler invoked for DELETE requests; executed with a `Negotiator` receiver and must return a `ResponseDTO`.
     * @return This `PageHandler` instance to allow fluent chaining.
     */
    infix fun DELETE(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.DELETE] = body }

    /**
 * Registers a handler for HTTP CONNECT requests.
 *
 * @param body Handler invoked with a `Negotiator` receiver; it receives the incoming request and the parsed query parameters and must produce the response DTO.
 * @return This `PageHandler` instance to allow fluent chaining.
 */
    infix fun CONNECT(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.CONNECT] = body }

    /**
 * Registers a handler for HTTP OPTIONS requests on this page.
 *
 * @param body Handler invoked with a `Negotiator` receiver that produces the `ResponseDTO` for the given `RequestDTO` and query parameters.
 * @return This `PageHandler` instance to allow fluent chaining.
 */
    infix fun OPTIONS(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.OPTIONS] = body }

    /**
 * Register a handler for HTTP TRACE requests on this page.
 *
 * @param body A handler with a `Negotiator` receiver that takes the incoming `RequestDTO` and the parsed query parameters `Map<String, String>`, and returns the `ResponseDTO` for TRACE requests.
 * @return This `PageHandler` instance for fluent chaining.
 */
    infix fun TRACE(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.TRACE] = body }

    /**
 * Register a handler for HTTP PATCH requests on this page.
 *
 * @param body Handler invoked with a `Negotiator` receiver, the incoming `RequestDTO`, and the parsed query parameters (`Map<String, String>`) to produce the `ResponseDTO`.
 * @return This `PageHandler` instance for fluent chaining.
 */
    infix fun PATCH(body: Negotiator.(RequestDTO, Map<String, String>) -> ResponseDTO): PageHandler = apply { responses[Method.PATCH] = body }
}
