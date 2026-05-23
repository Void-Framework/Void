package io.voidx.page

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.util.toResult
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Defines a route/page that produces a [ResponseDTO]. A page can declare metadata,
 * middleware hooks (before/after), and optional CSS resources.
 *
 * @param target The router path this page responds to (e.g. "/search").
 */
abstract class Page(
    open val target: String,
) {
    internal val relaysBefore = mutableListOf<Relay>()
    internal val relaysAfter = mutableListOf<Relay>()

    /**
     * Per-page, mutable bag for attaching values during processing.
     * Intended for internal use by middleware and handlers.
     */
    val attributes: MutableMap<String, Any> = mutableMapOf()

    /**
     * Produce a response for the incoming request and its query parameters.
     *
     * @param request The incoming request to handle.
     * @param queries Map of query parameter names to their values.
     * @return The response to send for the given request and queries.
     */
    abstract fun content(
        request: RequestDTO,
        queries: Map<String, String>,
    ): ResponseDTO

    /**
     * Registers a BEFORE middleware by class reference. The instance is created via reflection
     * and appended to this page's BEFORE chain. Higher [Relay.priority] values run first.
     */
    fun before(relay: KClass<RelayBefore>) {
        relaysBefore.add(relay.createInstance())
        relaysBefore.sortedByDescending { it.priority }
    }

    /**
     * Registers an instantiated BEFORE middleware. Higher [Relay.priority] values run first.
     */
    fun before(relay: RelayBefore) {
        relaysBefore.add(relay)
        relaysBefore.sortedByDescending { it.priority }
    }

    /**
     * Registers an AFTER middleware by class reference. The instance is created via reflection
     * and appended to this page's AFTER chain. Higher [Relay.priority] values run first.
     */
    fun after(relay: KClass<RelayAfter>) {
        relaysAfter.add(relay.createInstance())
        relaysAfter.sortedByDescending { it.priority }
    }

    /**
     * Registers an instantiated AFTER middleware. Higher [Relay.priority] values run first.
     */
    fun after(relay: RelayAfter) {
        relaysAfter.add(relay)
        relaysAfter.sortedByDescending { it.priority }
    }

    /**
     * Executes registered BEFORE middlewares and returns the first response produced by any middleware.
     *
     * The returned response, if any, will have its `_request` property set to the provided `request`.
     *
     * @param request The request to pass to BEFORE middlewares.
     * @return The first `ResponseDTO` produced by a BEFORE middleware with its `_request` set to the provided request, or `null` if none produced a response.
     */
    internal fun middlewareProcessBefore(request: RequestDTO): ResponseDTO? {
        relaysBefore.forEach {
            val newResponse = (it as? RelayBefore)?.processBefore(request.toResult())
            if (newResponse != null) {
                newResponse._request = request
                return newResponse
            }
        }
        return null
    }

    /**
     * Invokes every registered after middleware with the provided response.
     *
     * @param response The `Result<ResponseDTO>` produced by page handling, forwarded to each `RelayAfter`'s `processAfter`.
     */
    internal fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        relaysAfter.forEach {
            (it as? RelayAfter)?.processAfter(response)
        }
    }
}

/**
 * Base type for pages rendered when an exception occurs during request handling.
 * Receives an [Exception] on [content] evaluation.
 */
abstract class ExceptionPage : Page("")

/**
 * Base type for pages rendered when a route cannot be resolved (HTTP 404).
 */
abstract class NotFoundPage : Page("")

abstract class BadRequestPage : Page("")

/**
 * Returns a [PageHandler] for the static [path], creating and registering one
 * if it does not exist yet. Allows fluent per-method handlers, e.g.:
 * router.on("/api").GET { ... }
 */
fun route(
    path: String,
    builder: PageHandler.() -> Unit,
): PageHandler {
    val page = PageHandler(path)
    page.builder()
    return page
}

/**
 * Creates an ExceptionPage whose content is rendered by the provided block using the exception stored in the request attributes.
 *
 * @param block Receiver-style function invoked with the `Exception` extracted from `request.attributes["exception"]` and expected to return the page `ResponseDTO`.
 * @return An ExceptionPage instance that delegates its `content` to `block`.
 */
fun exceptionPage(block: ExceptionPage.(RequestDTO, Map<String, String>, Exception) -> ResponseDTO): ExceptionPage =
    object : ExceptionPage() {
        /**
         * Delegates page rendering to the configured exception handler by extracting the exception from the request.
         *
         * The exception is read from `request.attributes["exception"]` and passed to the handler block.
         *
         * @param request The incoming request which must contain the exception under the `"exception"` attribute.
         * @param queries Unused by this implementation.
         * @return The response produced by calling the exception handler with the extracted exception.
         */
        override fun content(
            request: RequestDTO,
            queries: Map<String, String>,
        ): ResponseDTO {
            val exception =
                request.attributes["exception"] as? Exception
                    ?: error("ExceptionPage requires request.attributes[\"exception\"]")
            return block(request, queries, exception)
        }
    }

/**
 * Creates a NotFoundPage that renders when no route matches the request.
 *
 * @param block Lambda executed as the page's `content()` to produce the response.
 * @return A NotFoundPage whose `content()` returns the `ResponseDTO` produced by `block`.
 */
fun notFoundPage(block: NotFoundPage.(request: RequestDTO, queries: Map<String, String>) -> ResponseDTO): NotFoundPage =
    object : NotFoundPage() {
        /**
         * Create the response for a not-found (404) route.
         *
         * @param request The incoming request DTO.
         * @param queries Map of query parameter names to values parsed from the request URI.
         * @return The response to send to the client.
         */
        override fun content(
            request: RequestDTO,
            queries: Map<String, String>,
        ) = block(request, queries)
    }

fun badRequestPage(block: BadRequestPage.(request: RequestDTO, queries: Map<String, String>) -> ResponseDTO): BadRequestPage =
    object : BadRequestPage() {
        /**
         * Create the response for a not-found (404) route.
         *
         * @param request The incoming request DTO.
         * @param queries Map of query parameter names to values parsed from the request URI.
         * @return The response to send to the client.
         */
        override fun content(
            request: RequestDTO,
            queries: Map<String, String>,
        ) = block(request, queries)
    }
