package io.void.html.page

import io.void.api.CssPage
import io.void.dto.http.RequestDTO
import io.void.html.Element
import io.void.html.metadata.Metadata
import io.void.html.metadata.metadata
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.metadata
import io.void.html.router.RouterUtil // force class loading to run ModuleInit.init()
import io.void.html.util.createResponse
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import java.util.UUID
import kotlin.collections.mutableListOf
import kotlin.collections.set

// Force RouterUtil object initialization so its ModuleInit.init() runs and hooks are registered.
@Suppress("unused")
private val ensureRouterUtilInit = RouterUtil

/** Optional HTML metadata associated with this page. */
var Page.metadata: Metadata?
    get() = attributes["metadata"] as? Metadata
    set(value) {
        attributes["metadata"] = value as Any
    }

/**
 * Defines a static HTML route at [path]. The page-level [\_metadata] builder runs once when the
 * page is created; the [block] is invoked per-request to build the root [Element] of the page.
 */
fun htmlRoute(
    path: String,
    _metadata: Metadata.() -> Unit,
    block: Page.(RequestDTO) -> Element,
): Page =
    object : Page(target = path) {
        init {
            metadata = metadata(this, _metadata)
        }

        override fun content() = createResponse(block(request), metadata as Metadata)
    }

/**
 * Defines an HTML exception page that renders when an error bubbles to the router. The [\_metadata]
 * builder runs once; the [block] is invoked with the thrown [Exception].
 */
fun exceptionPage(
    _metadata: Metadata.() -> Unit,
    block: ExceptionPage.(Exception) -> Element,
): ExceptionPage =
    object : ExceptionPage() {
        init {
            metadata = metadata(this, _metadata)
        }

        override fun content() = createResponse(block(exception), metadata as Metadata)
    }

/**
 * Defines an HTML 404 page that renders when no route matches. The [block] is invoked per-request.
 */
fun notFoundPage(
    _metadata: Metadata.() -> Unit,
    block: NotFoundPage.(RequestDTO) -> Element,
): NotFoundPage =
    object : NotFoundPage() {
        init {
            metadata = metadata(this, _metadata)
        }

        override fun content() = createResponse(block(request), metadata as Metadata)
    }

/**
 * Defines a dynamic HTML route at [path]. Path parameters are denoted with curly braces, e.g.
 * "/users/{id}" or optional as "/blog/{slug?}". The [block] receives a [DynamicPage] so you can
 * read path parameters via DynamicPage._data.
 */
fun dynamicHtmlRoute(
    path: String,
    _metadata: Metadata.() -> Unit,
    block: DynamicPage.(RequestDTO) -> Element,
): DynamicPage =
    object : DynamicPage(target = path) {
        init {
            metadata = metadata(this, _metadata)
        }

        override fun content() = createResponse(block(request), metadata as Metadata)
    }

/**
 * Marks one or more CSS resource files (by file name) to be included with this page.
 *
 * This looks up files under resources/css and remembers matches in [Page.cssFiles]. Actual
 * registration of those files as router pages and injection into HTML metadata happens in
 * [addCssToRouter]. Returns this page for fluent configuration.
 */
operator fun Page.invoke(vararg cssFileName: String): Page {
    listResourcePaths("css").forEach {
        if (it.split("/").last() in cssFileName) {
            cssFiles.add(it)
        }
    }
    return this
}

/**
 * Registers the previously selected CSS resource files ([Page.cssFiles]) as router pages and
 * injects their paths into this page's [metadata] as external stylesheets ([Metadata.externalCss]).
 *
 * For each discovered resource, a unique route like "/css/{uuid}/styles.css" is registered
 * by creating a [CssPage], and that URL is appended to the page's metadata so it is linked
 * in the final HTML head.
 */
internal fun Page.addCssToRouter(router: Router) {
    cssFiles.forEach {
        val uuid = UUID.randomUUID()
        router.addRoute(CssPage(uuid, readResourceText(it)))
        val path = "/css/$uuid/styles.css"
        metadata = this.metadata ?: metadata(this) { externalCss = mutableListOf(path) }
        metadata!!.externalCss =
            (metadata!!.externalCss ?: mutableListOf()).apply {
                add(path)
            }
    }
}
