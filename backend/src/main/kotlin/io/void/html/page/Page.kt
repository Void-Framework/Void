package io.void.html.page

import io.void.api.CssPage
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Defines a route/page that produces a [ContentType]. A page can declare metadata,
 * middleware hooks (before/after), and optional CSS resources.
 *
 * @param target The router path this page responds to (e.g. "/search").
 */
abstract class Page<T : ContentType>(
    open val target: String,
) {
    /** Maps elements to CSS class lists to apply at render time. */
    val classAttributes: MutableMap<Element, List<String>> = mutableMapOf()

    /** The current request bound to this page during handling. */
    lateinit var request: RequestDTO

    /** The resulting content type produced by this page. */
    abstract val contentType: KClass<T>

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
    abstract fun content(): T

/**
     * Registers CSS resources by file name present under resources/css.
     * Returns this page for fluent configuration.
     */
    operator fun invoke(vararg cssFileName: String): Page<T> {
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
    }

/** Registers a middleware instance to run before the page handler. */
    fun before(relay: RelayBefore) {
        relaysBefore.add(relay)
    }

/** Registers a middleware to run after the page handler by class reference. */
    fun after(relay: KClass<RelayAfter>) {
        relaysAfter.add(relay.createInstance())
    }

    /** Registers a middleware instance to run after the page handler. */
    fun after(relay: RelayAfter) {
        relaysAfter.add(relay)
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
        relaysBefore.forEach {
            (it as? RelayAfter)?.processAfter(response)
        }
    }
}

/**
 * Base type for pages rendered when an exception occurs during request handling.
 * The [exception] field is populated before [content] is evaluated.
 */
abstract class ExceptionPage<T : ContentType> : Page<T>("") {
    lateinit var exception: Exception
}

/**
 * Base type for pages rendered when a route cannot be resolved (HTTP 404).
 */
abstract class NotFoundPage<T : ContentType> : Page<T>("")

/**
 * Defines an HTML route at [path] with page-level [metadata] and a content [block]
 * that builds and returns the root [Element] for the response body.
 */
fun htmlRoute(
    path: String,
    metadata: Metadata.() -> Unit,
    block: Page<ContentType.HtmlElements>.(RequestDTO) -> Element,
): Page<ContentType.HtmlElements> =
    object : Page<ContentType.HtmlElements>(target = path) {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata
        override val contentType = ContentType.HtmlElements::class

        override fun content() = ContentType.HtmlElements(block(request), _metadata)
    }

/**
 * Defines an API route at [path] that returns a raw [ResponseDTO] via [block].
 */
fun apiRoute(
    path: String,
    block: Page<ContentType.Response>.(RequestDTO) -> ResponseDTO,
): Page<ContentType.Response> =
    object : Page<ContentType.Response>(target = path) {
        override var metadata: Metadata? = null
        override val contentType = ContentType.Response::class

        override fun content() = ContentType.Response(block(request))
    }

/**
 * Defines a page to render when an exception occurs, producing HTML content with [metadata].
 */
fun exceptionPage(
    metadata: Metadata.() -> Unit,
    block: ExceptionPage<ContentType.HtmlElements>.(Exception) -> Element,
): ExceptionPage<ContentType.HtmlElements> =
    object : ExceptionPage<ContentType.HtmlElements>() {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata
        override val contentType = ContentType.HtmlElements::class

        override fun content() = ContentType.HtmlElements(block(exception), _metadata)
    }

/**
 * Defines an exception page that returns a raw [ResponseDTO] via [block].
 */
fun exceptionPage(block: ExceptionPage<ContentType.Response>.(Exception) -> ResponseDTO): ExceptionPage<ContentType.Response> =
    object : ExceptionPage<ContentType.Response>() {
        override var metadata: Metadata? = null
        override val contentType = ContentType.Response::class

        override fun content() = ContentType.Response(block(exception))
    }

fun notFoundPage(
    metadata: Metadata.() -> Unit,
    block: NotFoundPage<ContentType.HtmlElements>.(RequestDTO) -> Element,
): NotFoundPage<ContentType.HtmlElements> =
    object : NotFoundPage<ContentType.HtmlElements>() {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata
        override val contentType = ContentType.HtmlElements::class

        override fun content() = ContentType.HtmlElements(block(request), _metadata)
    }

fun notFoundPage(block: NotFoundPage<ContentType.Response>.(RequestDTO) -> ResponseDTO): NotFoundPage<ContentType.Response> =
    object : NotFoundPage<ContentType.Response>() {
        override var metadata: Metadata? = null
        override val contentType = ContentType.Response::class

        override fun content() = ContentType.Response(block(request))
    }
