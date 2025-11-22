package io.voidx.html.page

import io.voidx.css.CssPage
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.html.Element
import io.voidx.html.fractal
import io.voidx.html.metadata.Metadata
import io.voidx.html.metadata.metadata
import io.voidx.page.DynamicPage
import io.voidx.html.router.RouterUtil
import io.voidx.html.util.createResponse
import io.voidx.page.ExceptionPage
import io.voidx.page.NotFoundPage
import io.voidx.page.Page
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import io.voidx.util.readResourceText
import java.util.*

// Force RouterUtil object initialization so its ModuleInit.init() runs and hooks are registered.
@Suppress("unused")
private val ensureRouterUtilInit = RouterUtil

/** Optional HTML metadata associated with this page. */
var Page.metadata: Metadata?
    get() = attributes["metadata"] as? Metadata
    set(value) {
        attributes["metadata"] = value as Any
    }

fun Page.html(builder: Element.() -> Unit): ResponseDTO {
    val fractal = fractal(builder)
    return createResponse(fractal, metadata ?: metadata(this) {})
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
