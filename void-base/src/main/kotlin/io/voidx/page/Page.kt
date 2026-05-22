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

    /** Builds the concrete [ResponseDTO] instance to be returned. */
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
     * Execute registered BEFORE middlewares and return the first middleware-produced response.
     *
     * If a middleware returns a non-null ResponseDTO, that response will have its `_request`
     * property set to this page's current request before being returned.
     *
     * @return The first `ResponseDTO` produced by a BEFORE middleware with its `_request` set, or `null` if none produced a response.
     */
    fun middlewareProcessBefore(request: RequestDTO): ResponseDTO? {
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
     * Runs all registered [RelayAfter] middlewares with the produced [response].
     */
    internal fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        relaysAfter.forEach {
            (it as? RelayAfter)?.processAfter(response)
        }
    }
}

/**
 * Base type for pages rendered when an exception occurs during request handling.
 * The [exception] field is populated before [content] is evaluated.
 */
abstract class ExceptionPage : Page("")

/**
 * Base type for pages rendered when a route cannot be resolved (HTTP 404).
 */
abstract class NotFoundPage : Page("")

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
 * Defines an exception page that returns a raw [ResponseDTO] via [block].
 */
fun exceptionPage(block: ExceptionPage.(Exception) -> ResponseDTO): ExceptionPage =
    object : ExceptionPage() {
        override fun content(
            request: RequestDTO,
            queries: Map<String, String>,
        ): ResponseDTO = block(request.attributes["exception"] as Exception)
    }

/**
 * Defines a 404 page rendered when no route matches the request.
 * The [block] is invoked to produce a raw [ResponseDTO].
 */
fun notFoundPage(block: NotFoundPage.(request: RequestDTO, queries: Map<String, String>) -> ResponseDTO): NotFoundPage =
    object : NotFoundPage() {
        override fun content(
            request: RequestDTO,
            queries: Map<String, String>,
        ) = block(request, queries)
    }
