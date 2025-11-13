package io.void.html.page

import io.void.api.CssPage
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import java.util.*
import kotlin.collections.sortedByDescending
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Defines a route/page that produces a [ContentType]. A page can declare metadata,
 * middleware hooks (before/after), and optional CSS resources.
 *
 * @param target The router path this page responds to (e.g. "/search").
 */
abstract class Page(
    open val target: String,
) {
    /** List of css classes. */
    val classAttributes: MutableList<String> = mutableListOf()

    /** The current request bound to this page during handling. */
    lateinit var request: RequestDTO

    val cssFiles = mutableListOf<String>()
    internal val relaysBefore = mutableListOf<Relay>()
    internal val relaysAfter = mutableListOf<Relay>()

    /**
     * Per-page, mutable bag for attaching values during processing.
     * Intended for internal use by middleware and handlers.
     */
    val attributes: MutableMap<String, Any> = mutableMapOf()

    /** Whether to include the compiled Tailwind. */
    val includeTailwind = true

    /** Whether to include the kts script. */
    val includeKts = true

    /** URL query parameters for the current request. */
    lateinit var queries: Map<String, String>

    /** Builds the concrete [ContentType] instance to be rendered or returned. */
    abstract fun content(): ResponseDTO

    /** Registers a middleware to run before the page handler by class reference. */
    fun before(relay: KClass<RelayBefore>) {
        relaysBefore.add(relay.createInstance())
        relaysBefore.sortedByDescending { it.priority }
    }

    /** Registers a middleware instance to run before the page handler. */
    fun before(relay: RelayBefore) {
        relaysBefore.add(relay)
        relaysBefore.sortedByDescending { it.priority }
    }

    /** Registers a middleware to run after the page handler by class reference. */
    fun after(relay: KClass<RelayAfter>) {
        relaysAfter.add(relay.createInstance())
        relaysAfter.sortedByDescending { it.priority }
    }

    /** Registers a middleware instance to run after the page handler. */
    fun after(relay: RelayAfter) {
        relaysAfter.add(relay)
        relaysAfter.sortedByDescending { it.priority }
    }

    /**
     * Runs all registered [RelayBefore] middlewares. If any returns a non-null [ResponseDTO],
     * the processing is short-circuited and that response is returned.
     */
    fun middlewareProcessBefore(requestDTO: Result<RequestDTO>): ResponseDTO? {
        relaysBefore.forEach {
            val newResponse = (it as? RelayBefore)?.processBefore(requestDTO)
            if (newResponse != null) {
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
abstract class ExceptionPage : Page("") {
    lateinit var exception: Exception
}

/**
 * Base type for pages rendered when a route cannot be resolved (HTTP 404).
 */
abstract class NotFoundPage : Page("")

/**
 * Defines an API route at [path] that returns a raw [ResponseDTO] via [block].
 */
fun apiRoute(
    path: String,
    block: Page.(RequestDTO) -> ResponseDTO,
): Page =
    object : Page(target = path) {
        override fun content() = block(request)
    }

/**
 * Defines an exception page that returns a raw [ResponseDTO] via [block].
 */
fun exceptionPage(block: ExceptionPage.(Exception) -> ResponseDTO): ExceptionPage =
    object : ExceptionPage() {
        override fun content() = block(exception)
    }

fun notFoundPage(block: NotFoundPage.(RequestDTO) -> ResponseDTO): NotFoundPage =
    object : NotFoundPage() {
        override fun content() = block(request)
    }
