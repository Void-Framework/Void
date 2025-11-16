package io.voidx.router.util

import io.voidx.clienthandler.ClientHandler
import io.voidx.dto.http.RequestDTO
import io.voidx.dto.http.ResponseDTO
import io.voidx.html.page.Page
import io.voidx.html.page.dynamic.DynamicPage
import io.voidx.html.page.dynamic.Path
import io.voidx.router.toResult
import java.util.concurrent.ConcurrentHashMap

/**
 * Internal helpers for routing requests to the correct handler and building
 * responses for different page types (HTML, API, KTS, dynamic).
 */
internal interface RequestHandler {
    val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage>

    companion object {
        private val segmentRegex = Regex("""^\{([^{}]+)}$""")
        private val optionalSegment = Regex("""^\{([^{}?]+)\?}$""")
    }

    /**
     * Attempts to resolve the incoming [requestDTO] against registered dynamic routes.
     *
     * Matching rules:
     * - Route patterns are tokenized by '/'. Static segments must match exactly.
     * - Dynamic segments use "{name}" syntax and accept any single path token; the
     *   captured values are exposed to the page in [DynamicPage._data].
     * - Optional trailing segments use "{name?}" and may be omitted by the request.
     * - A single trailing slash at the end of either the request or pattern is ignored.
     *
     * If a route matches, the page's request context and [query] map are populated, the
     * page BEFORE middleware is executed, and finally [DynamicPage.content] is produced.
     *
     * @return a [ResponseDTO] when a dynamic route matches; null otherwise so callers can
     *         fall back to static routes or 404 handling.
     */
    fun handleDynamic(
        requestDTO: RequestDTO,
        query: Map<String, String>,
    ): ResponseDTO? {
        val requestTarget = requestDTO.target
        if (requestTarget.endsWith("favicon.ico")) return null
        val url = requestTarget.split('/').toMutableList()
        dynamicRoutes.forEach { (target, route) ->
            val dynamics = mutableMapOf<Path, String>()
            val mutableTarget = target.toMutableList()
            url.trimTrailingEmpty()
            mutableTarget.trimTrailingEmpty()
            if (url.size != mutableTarget.size) {
                if (mutableTarget.isNotEmpty() &&
                    mutableTarget
                        .last()
                        .matches(optionalSegment) && url.size + 1 == mutableTarget.size
                ) {
                    mutableTarget.removeLast()
                } else {
                    return@forEach
                }
            }
            url.forEachIndexed { i, segment ->
                val targetValue = mutableTarget[i]
                if (segment == targetValue) {
                    return@forEachIndexed
                } else if (segmentRegex.matches(targetValue)) {
                    val match = segmentRegex.matchEntire(targetValue)!!.groupValues[1]
                    dynamics[match] = url[i]
                } else {
                    return@forEach
                }
            }

            route._data = dynamics
            route.request = requestDTO
            route.queries = query

            val response = route.middlewareProcessBefore(requestDTO.toResult())

            return response ?: route.content()
        }
        return null
    }

    /**
     * Returns a [ResponseDTO] for a static [page], consulting the cache when present.
     * If the target path exists in the [io.voidx.cache.Cache], the cached value is
     * returned; otherwise [Page.content] is evaluated.
     */
    fun handleResponse(
        page: Page,
        clientHandler: ClientHandler,
        target: String,
    ): ResponseDTO =
        page.content()
}

/**
 * Removes a single trailing empty string element from this list if present.
 * Useful when splitting URL paths to ignore a trailing slash.
 *
 * @return true if an empty element was removed; false otherwise.
 */
fun MutableList<String>.trimTrailingEmpty(): Boolean {
    val hasEmptyTail = this.lastOrNull()?.isEmpty() == true
    if (hasEmptyTail) removeAt(lastIndex)
    return hasEmptyTail
}
