package io.void.html.page

import io.void.api.CssPage
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import io.void.util.createResponse
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

    /** Optional HTML metadata associated with this page. */
    abstract var metadata: Metadata?
    private val cssFiles = mutableListOf<String>()
    internal val relaysBefore = mutableListOf<Relay>()
    internal val relaysAfter = mutableListOf<Relay>()

    /** Whether to include the compiled Tailwind. */
    val includeTailwind = true

    /** Whether to include the kts script. */
    val includeKts = true

    /** URL query parameters for the current request. */
    lateinit var queries: Map<String, String>

    /** Builds the concrete [ContentType] instance to be rendered or returned. */
    abstract fun content(): ResponseDTO

    /**
     * Registers CSS resources by file name present under resources/css.
     * Returns this page for fluent configuration.
     */
    operator fun invoke(vararg cssFileName: String): Page {
        listResourcePaths("css").forEach {
            if (it.split("/").last() in cssFileName) {
                cssFiles.add(it)
            }
        }
        return this
    }

    /**
     * Registers the previously selected CSS resources as router pages and
     * injects their paths into this page's [metadata] as external stylesheets.
     */
    internal fun addCssToRouter(router: Router) {
        cssFiles.forEach {
            val uuid = UUID.randomUUID()
            router.addRoute(CssPage(uuid, readResourceText(it)))
            val path = "/css/$uuid/styles.css"
            metadata = metadata ?: metadata(this) { externalCss = mutableListOf(path) }
            metadata!!.externalCss =
                (metadata!!.externalCss ?: mutableListOf()).apply {
                    add(path)
                }
        }
    }

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
    internal fun middlewareProcessBefore(requestDTO: Result<RequestDTO>): ResponseDTO? {
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
 * Defines an HTML route at [path] with page-level [metadata] and a content [block]
 * that builds and returns the root [Element] for the response body.
 */
fun htmlRoute(
    path: String,
    metadata: Metadata.() -> Unit,
    block: Page.(RequestDTO) -> Element,
): Page =
    object : Page(target = path) {
        override var metadata: Metadata? = metadata(this) { }.apply(metadata)

        override fun content() = createResponse(block(request), this.metadata!!)
    }

/**
 * Defines an API route at [path] that returns a raw [ResponseDTO] via [block].
 */
fun apiRoute(
    path: String,
    block: Page.(RequestDTO) -> ResponseDTO,
): Page =
    object : Page(target = path) {
        override var metadata: Metadata? = null

        override fun content() = block(request)
    }

/**
 * Defines a page to render when an exception occurs, producing HTML content with [metadata].
 */
fun exceptionPage(
    metadata: Metadata.() -> Unit,
    block: ExceptionPage.(Exception) -> Element,
): ExceptionPage =
    object : ExceptionPage() {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata

        override fun content() = createResponse(block(exception), this.metadata!!)
    }

/**
 * Defines an exception page that returns a raw [ResponseDTO] via [block].
 */
fun exceptionPage(block: ExceptionPage.(Exception) -> ResponseDTO): ExceptionPage =
    object : ExceptionPage() {
        override var metadata: Metadata? = null

        override fun content() = block(exception)
    }

fun notFoundPage(
    metadata: Metadata.() -> Unit,
    block: NotFoundPage.(RequestDTO) -> Element,
): NotFoundPage =
    object : NotFoundPage() {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata

        override fun content() = createResponse(block(request), this.metadata!!)
    }

fun notFoundPage(block: NotFoundPage.(RequestDTO) -> ResponseDTO): NotFoundPage =
    object : NotFoundPage() {
        override var metadata: Metadata? = null

        override fun content() = block(request)
    }
