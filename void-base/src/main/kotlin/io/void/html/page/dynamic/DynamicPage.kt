package io.void.html.page.dynamic

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page

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
        override fun content() = block(request)
    }
