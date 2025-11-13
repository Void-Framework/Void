package io.void.html.page

import io.void.api.CssPage
import io.void.dto.http.RequestDTO
import io.void.html.Element
import io.void.html.metadata.Metadata
import io.void.html.metadata.metadata
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.metadata
import io.void.html.util.createResponse
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import java.util.UUID
import kotlin.collections.mutableListOf
import kotlin.collections.set

/** Optional HTML metadata associated with this page. */
var Page.metadata: Metadata?
    get() = attributes["metadata"] as? Metadata
    set(value) {
        attributes["metadata"] = value as Any
    }

/**
 * Defines an HTML route at [path] with page-level [_metadata] and a content [block]
 * that builds and returns the root [Element] for the response body.
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
 * Defines a page to render when an exception occurs, producing HTML content with [metadata].
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
 * Defines a dynamic HTML route at [path] with page-level [metadata] and a content [block]
 * that returns the root [Element] to render.
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
 * Registers CSS resources by file name present under resources/css.
 * Returns this page for fluent configuration.
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
 * Registers the previously selected CSS resources as router pages and
 * injects their paths into this page's [metadata] as external stylesheets.
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