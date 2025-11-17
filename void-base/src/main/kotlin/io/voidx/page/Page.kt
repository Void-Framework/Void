package io.voidx.page

import androidx.compose.runtime.Composable
import io.voidx.dto.http.RequestDTO
import io.voidx.dto.http.ResponseDTO
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
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

    /**
     * Names of external CSS resource files to include for this page.
     *
     * Use the Page invocation operator (page("style.css")) to populate this list with
     * resources discovered under resources/css. See [io.voidx.html.page.addCssToRouter].
     */
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
    @Composable
    abstract fun content(): ResponseDTO

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

/**
 * Defines a 404 page rendered when no route matches the request.
 * The [block] is invoked to produce a raw [ResponseDTO].
 */
fun notFoundPage(block: NotFoundPage.(RequestDTO) -> ResponseDTO): NotFoundPage =
    object : NotFoundPage() {
        override fun content() = block(request)
    }
