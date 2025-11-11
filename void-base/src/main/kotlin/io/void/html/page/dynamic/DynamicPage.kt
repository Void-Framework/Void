package io.void.html.page.dynamic

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import io.void.util.createResponse

typealias Path = String

/**
 * Page base for routes with dynamic path segments.
 *
 * Use curly braces to declare dynamic parameters:
 * - Required segment: {id}  e.g. /users/{id}
 * - Optional trailing segment: {slug?} (must be last) e.g. /blog/{slug?}
 *
 * Resolved values are exposed via [data] or the index operator (e.g. this["id"]).
 */
abstract class DynamicPage(
    target: String,
) : Page(target = target) {
    internal var _data = mutableMapOf<Path, String>()

    /** Map of dynamic path segment name to value for the current request. */
    val data: Map<Path, String> get() = _data

    /** Shortcut for [data][Map.get] to retrieve a segment by name. */
    operator fun get(segment: Path) = data[segment]
}

/** Type-safe accessor for a dynamic path segment named [name], cast to [T] when possible. */
inline fun <reified T : Any> DynamicPage.path(name: String): T? = data[name] as? T

/**
 * Defines a dynamic API route at [path]. The [block] returns a [ResponseDTO] for the given request.
 */
fun dynamicApiRoute(
    path: String,
    block: DynamicPage.(RequestDTO) -> ResponseDTO,
): DynamicPage =
    object : DynamicPage(target = path) {
        override var metadata: Metadata? = null

        override fun content() = block(request)
    }

/**
 * Defines a dynamic HTML route at [path] with page-level [metadata] and a content [block]
 * that returns the root [Element] to render.
 */
fun dynamicHtmlRoute(
    path: String,
    metadata: Metadata.() -> Unit,
    block: DynamicPage.(RequestDTO) -> Element,
): DynamicPage =
    object : DynamicPage(target = path) {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata

        override fun content() = createResponse(block(request), this.metadata!!)
    }
